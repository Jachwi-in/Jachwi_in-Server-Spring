package com.capstone.Jachwi_inServerSpring.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.capstone.Jachwi_inServerSpring.client.FastApiClient;
import com.capstone.Jachwi_inServerSpring.domain.Building;
import com.capstone.Jachwi_inServerSpring.domain.dto.PostClassifyRequestDto;
import com.capstone.Jachwi_inServerSpring.domain.dto.RoomRecommendRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmService {

    private final AnthropicClient anthropicClient;
    private final MapService mapService;
    private final FastApiClient fastApiClient;
    private final StringRedisTemplate redisTemplate;

    private static final String CLASSIFY_CACHE_PREFIX = "llm:classify:";
    private static final String RECOMMEND_CACHE_PREFIX = "llm:recommend:";
    private static final Duration CACHE_TTL = Duration.ofHours(1);

    // ─────────────────────────────────────────────
    // 1. 게시글 분류
    // ─────────────────────────────────────────────
    public String classifyPost(PostClassifyRequestDto dto) {
        String cacheKey = CLASSIFY_CACHE_PREFIX + md5(dto.getTitle() + dto.getContent());
        String cached = getCache(cacheKey);
        if (cached != null) {
            log.info("[LLM 캐시 히트] 게시글 분류");
            return cached;
        }

        String prompt = """
                아래 자취 커뮤니티 게시글을 읽고, 다음 카테고리 중 하나만 골라서 카테고리 이름만 답해줘. 다른 말은 하지 마.
                카테고리: 질문, 후기, 자취팁, 정보공유, 기타

                제목: %s
                내용: %s
                """.formatted(dto.getTitle(), dto.getContent());

        String result = callClaude(prompt, 50);
        saveCache(cacheKey, result);
        return result;
    }

    // ─────────────────────────────────────────────
    // 2. 자취방 추천
    // ─────────────────────────────────────────────
    public String recommendRooms(RoomRecommendRequestDto dto) {
        String cacheKey = RECOMMEND_CACHE_PREFIX + md5(dto.toString());
        String cached = getCache(cacheKey);
        if (cached != null) {
            log.info("[LLM 캐시 히트] 자취방 추천");
            return cached;
        }

        // 1순위: FastAPI 벡터 검색 (사용자 조건 자연어 → 유사 건물)
        String searchQuery = buildSearchQuery(dto);
        List<Map<String, Object>> vectorBuildings = fastApiClient.searchBuildings(searchQuery, 10);

        // FastAPI 장애 시 DB 좌표 범위 검색으로 폴백
        List<Map<String, Object>> candidates;
        if (!vectorBuildings.isEmpty()) {
            log.info("[LLM] FastAPI 벡터 검색 결과 {}건 사용", vectorBuildings.size());
            candidates = vectorBuildings;
        } else {
            log.info("[LLM] FastAPI 폴백 — DB 좌표 범위 검색 사용");
            List<Building> dbBuildings = mapService.getBuildingsInArea(
                    dto.getCenterX() - dto.getRadius(), dto.getCenterX() + dto.getRadius(),
                    dto.getCenterY() - dto.getRadius(), dto.getCenterY() + dto.getRadius()
            );
            if (dbBuildings.isEmpty()) {
                return "해당 지역에서 검색된 매물이 없습니다.";
            }
            candidates = dbBuildings.stream()
                    .limit(10)
                    .map(b -> Map.<String, Object>of(
                            "시도명", b.getProvince() != null ? b.getProvince() : "",
                            "시군구", b.getDistrict() != null ? b.getDistrict() : "",
                            "도로명", b.getStreetName() != null ? b.getStreetName() : "",
                            "편의점", b.getConvenienceStore(),
                            "카페", b.getCafe(),
                            "CCTV", b.getCctv(),
                            "버스정류장", b.getBusStop(),
                            "학교_거리", b.getSchoolDistance(),
                            "가로등", b.getStreetLight()
                    ))
                    .toList();
        }

        if (candidates.isEmpty()) {
            return "해당 지역에서 검색된 매물이 없습니다.";
        }

        String buildingList = candidates.stream()
                .map(b -> "- 주소: %s %s %s, 편의점: %s, 카페: %s, CCTV: %s, 버스정류장: %s, 학교거리: %sm, 가로등: %s"
                        .formatted(
                                b.getOrDefault("시도명", ""), b.getOrDefault("시군구", ""), b.getOrDefault("도로명", ""),
                                b.getOrDefault("편의점", 0), b.getOrDefault("카페", 0), b.getOrDefault("CCTV", 0),
                                b.getOrDefault("버스정류장", 0), b.getOrDefault("학교_거리", 0), b.getOrDefault("가로등", 0)
                        ))
                .collect(Collectors.joining("\n"));

        String prefs = dto.getPreferences() == null || dto.getPreferences().isEmpty()
                ? "특별한 선호 없음"
                : String.join(", ", dto.getPreferences());

        String prompt = """
                자취방을 구하는 학생을 위한 추천을 해줘.

                [사용자 조건]
                - 인근 학교: %s
                - 선호 편의시설: %s

                [검색된 건물 목록]
                %s

                위 목록에서 조건에 가장 잘 맞는 건물 3곳을 추천하고, 각 건물마다 추천 이유를 한 줄로 설명해줘.
                """.formatted(dto.getSchool(), prefs, buildingList);

        String result = callClaude(prompt, 1024);
        saveCache(cacheKey, result);
        return result;
    }

    // ─────────────────────────────────────────────
    // FastAPI 검색 쿼리 생성
    // ─────────────────────────────────────────────
    private String buildSearchQuery(RoomRecommendRequestDto dto) {
        String prefs = (dto.getPreferences() == null || dto.getPreferences().isEmpty())
                ? "편의시설 무관"
                : String.join(", ", dto.getPreferences());
        return "%s 근처 자취방, 선호시설: %s".formatted(dto.getSchool(), prefs);
    }

    // ─────────────────────────────────────────────
    // Claude API 호출 공통 메서드
    // ─────────────────────────────────────────────
    private String callClaude(String userPrompt, long maxTokens) {
        MessageCreateParams params = MessageCreateParams.builder()
                .model(Model.CLAUDE_OPUS_4_6)
                .maxTokens(maxTokens)
                .addUserMessage(userPrompt)
                .build();

        Message response = anthropicClient.messages().create(params);

        return response.content().stream()
                .flatMap(block -> block.text().stream())
                .map(textBlock -> textBlock.text())
                .collect(Collectors.joining());
    }

    // ─────────────────────────────────────────────
    // Redis 캐시 유틸
    // ─────────────────────────────────────────────
    private String getCache(String key) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        return ops.get(key);
    }

    private void saveCache(String key, String value) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, value, CACHE_TTL);
    }

    private String md5(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(input.hashCode());
        }
    }
}
