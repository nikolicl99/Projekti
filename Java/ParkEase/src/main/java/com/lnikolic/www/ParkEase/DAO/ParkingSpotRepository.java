package com.lnikolic.www.ParkEase.DAO;

import com.lnikolic.www.ParkEase.Entity.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Integer> {
}
