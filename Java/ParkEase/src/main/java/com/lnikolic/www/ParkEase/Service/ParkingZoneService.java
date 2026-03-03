package com.lnikolic.www.ParkEase.Service;

import com.lnikolic.www.ParkEase.DAO.ParkingZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingZoneService {
    private final ParkingZoneRepository parkingZoneRepository;
}
