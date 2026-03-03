package com.lnikolic.www.ParkEase.Service;

import com.lnikolic.www.ParkEase.DAO.ParkingPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingPlaceService {
    private final ParkingPlaceRepository parkingPlaceRepository;
}
