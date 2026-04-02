package com.capstone.Jachwi_inServerSpring.service;

import com.capstone.Jachwi_inServerSpring.config.KafkaConfig;
import com.capstone.Jachwi_inServerSpring.domain.dto.LocationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationProducer {

    private final KafkaTemplate<String, LocationDto> kafkaTemplate;

    public void sendLocation(LocationDto locationDto) {
        kafkaTemplate.send(KafkaConfig.LOCATION_TOPIC, locationDto.getUserId(), locationDto);
        log.info("위치 이벤트 발행 - userId: {}, x: {}, y: {}", locationDto.getUserId(), locationDto.getX(), locationDto.getY());
    }
}
