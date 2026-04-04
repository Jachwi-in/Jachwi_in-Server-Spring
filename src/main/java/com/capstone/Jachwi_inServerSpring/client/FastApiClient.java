package com.capstone.Jachwi_inServerSpring.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FastApiClient {

    private final RestTemplate restTemplate;

    @Value("${fastapi.base-url}")
    private String baseUrl;

    /**
     * 자연어 쿼리로 유사 건물 벡터 검색
     * @return 건물 payload 목록 (한글 키: 시도명, 편의점, ...)
     *         FastAPI 장애 시 빈 리스트 반환 (fallback)
     */
    public List<Map<String, Object>> searchBuildings(String query, int topK) {
        try {
            FastApiSearchRequest request = new FastApiSearchRequest(query, topK);
            ResponseEntity<FastApiSearchResponse> response = restTemplate.postForEntity(
                    baseUrl + "/search",
                    request,
                    FastApiSearchResponse.class
            );

            if (response.getBody() == null || response.getBody().getResults() == null) {
                return Collections.emptyList();
            }

            return response.getBody().getResults().stream()
                    .map(FastApiSearchResponse.SearchResult::getBuilding)
                    .toList();

        } catch (RestClientException e) {
            log.warn("[FastAPI] 벡터 검색 실패 — DB 폴백 사용. 원인: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
