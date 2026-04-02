package com.capstone.Jachwi_inServerSpring.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private String userId;
    private double x;       // 경도 (longitude)
    private double y;       // 위도 (latitude)
    private double radius;  // 검색 반경 (단위: 좌표 기준)
}
