package com.capstone.Jachwi_inServerSpring.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LlmService {

    private final AnthropicClient anthropicClient;
    private final MapService mapService;
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

        // DB에서 주변 건물 조회
        List<Building> buildings = mapService.getBuildingsInArea(
                dto.getCenterX() - dto.getRadius(), dto.getCenterX() + dto.getRadius(),
                dto.getCenterY() - dto.getRadius(), dto.getCenterY() + dto.getRadius()
        );

        if (buildings.isEmpty()) {
            return "해당 지역에서 검색된 매물이 없습니다.";
        }

        // 상위 10개만 프롬프트에 포함 (토큰 절약)
        String buildingList = buildings.stream()
                .limit(10)
                .map(b -> "- 주소: %s %s %s, 편의점: %d, 카페: %d, CCTV: %d, 버스정류장: %d, 학교거리: %.0fm, 가로등: %d"
                        .formatted(b.getProvince(), b.getDistrict(), b.getStreetName(),
                                b.getConvenienceStore(), b.getCafe(), b.getCctv(),
                                b.getBusStop(), b.getSchoolDistance(), b.getStreetLight()))
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
