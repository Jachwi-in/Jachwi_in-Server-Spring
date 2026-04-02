package com.capstone.Jachwi_inServerSpring.service;

import com.capstone.Jachwi_inServerSpring.config.KafkaConfig;
import com.capstone.Jachwi_inServerSpring.domain.Building;
import com.capstone.Jachwi_inServerSpring.domain.dto.LocationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationConsumer {

    private final MapService mapService;

    @KafkaListener(topics = KafkaConfig.LOCATION_TOPIC, groupId = "jachwi-location-group")
    public void consume(LocationDto locationDto) {
        log.info("위치 이벤트 수신 - userId: {}, x: {}, y: {}", locationDto.getUserId(), locationDto.getX(), locationDto.getY());

        double x = locationDto.getX();
        double y = locationDto.getY();
        double radius = locationDto.getRadius();

        // 반경 기반으로 좌표 범위 계산 후 주변 건물 조회
        List<Building> nearbyBuildings = mapService.getBuildingsInArea(
                x - radius, x + radius,
                y - radius, y + radius
        );

        log.info("주변 매물 {}건 조회 완료 - userId: {}", nearbyBuildings.size(), locationDto.getUserId());
        // 실제 서비스에서는 여기서 WebSocket 등으로 클라이언트에 push
    }
}
