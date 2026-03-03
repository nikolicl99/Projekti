package com.lnikolic.www.ParkEase.Service;

import com.lnikolic.www.ParkEase.DAO.ParkingSpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingSpotService {
    private final ParkingSpotRepository parkingSpotRepository;
}
