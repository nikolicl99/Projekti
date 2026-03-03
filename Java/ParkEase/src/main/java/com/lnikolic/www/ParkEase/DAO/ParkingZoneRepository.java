package com.lnikolic.www.ParkEase.DAO;

import com.lnikolic.www.ParkEase.Entity.ParkingZone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingZoneRepository extends JpaRepository<ParkingZone, Integer> {
}
