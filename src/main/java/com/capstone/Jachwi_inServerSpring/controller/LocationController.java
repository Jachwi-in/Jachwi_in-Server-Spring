package com.capstone.Jachwi_inServerSpring.controller;

import com.capstone.Jachwi_inServerSpring.domain.dto.LocationDto;
import com.capstone.Jachwi_inServerSpring.service.LocationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/location")
@CrossOrigin(origins = "http://localhost:62328")
public class LocationController {

    private final LocationProducer locationProducer;

    // 클라이언트가 현재 위치를 전송 → Kafka 토픽에 발행
    @PostMapping("/send")
    public ResponseEntity<String> sendLocation(@RequestBody LocationDto locationDto) {
        locationProducer.sendLocation(locationDto);
        return ResponseEntity.ok("위치 이벤트 발행 완료");
    }
}
