package com.capstone.Jachwi_inServerSpring.domain;

import com.capstone.Jachwi_inServerSpring.domain.cpk.BuildingCpk;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional
@Table
@IdClass(BuildingCpk.class)
public class Building implements Serializable {

    @Id
    private double x;
    @Id
    private double y;
    @Column
    private String 시도명;
    @Column
    private String 시군구;
    @Column
    private String 법정읍면동명;
    @Column
    private String 도로명;
    @Column
    private int 건물본번;
    @Column
    private int 건물부번;
    @Column
    private String 건축물대장_건물명;
    @Column
    private String 상세건물명;
    @Column
    private String 시군구용_건물명;
    @Column
    private int 버스정류장;
    @Column
    private int 편의점;
    @Column
    private int 카페;
    @Column
    private int 가로등;
    @Column
    private int CCTV;
    @Column
    private double 학교_거리;
    @Column
    private int 병원;
    @Column
    private int 식당;

}
