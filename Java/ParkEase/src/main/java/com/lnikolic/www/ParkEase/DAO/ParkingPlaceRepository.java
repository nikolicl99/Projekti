package com.lnikolic.www.ParkEase.DAO;

import com.lnikolic.www.ParkEase.Entity.ParkingPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingPlaceRepository extends JpaRepository<ParkingPlace, Integer> {
}
