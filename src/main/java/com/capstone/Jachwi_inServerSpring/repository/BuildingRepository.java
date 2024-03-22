package com.capstone.Jachwi_inServerSpring.repository;

import com.capstone.Jachwi_inServerSpring.domain.Building;
import com.capstone.Jachwi_inServerSpring.domain.cpk.BuildingCpk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, BuildingCpk> {

    List<Building> findByXBetweenAndYBetween(Double minX, Double maxX, Double minY, Double maxY);

}
