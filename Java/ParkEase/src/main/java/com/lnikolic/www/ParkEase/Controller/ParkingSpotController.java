package com.lnikolic.www.ParkEase.Controller;

import com.lnikolic.www.ParkEase.Service.ParkingSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parking_spot")
public class ParkingSpotController {

    @Autowired
    private ParkingSpotService parkingSpotService;
}
