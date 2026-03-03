package com.lnikolic.www.ParkEase.Service;

import com.lnikolic.www.ParkEase.DAO.VehiclesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehiclesService {
    private final VehiclesRepository vehiclesRepository;
}
