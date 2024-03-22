package com.capstone.Jachwi_inServerSpring.domain.cpk;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BuildingCpk implements Serializable {
    @Column
    private Double x;
    private Double y;
}
