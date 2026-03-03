package com.lnikolic.www.ParkEase.DAO;

import com.lnikolic.www.ParkEase.Entity.Vehicles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehiclesRepository extends JpaRepository<Vehicles, Integer> {
}
