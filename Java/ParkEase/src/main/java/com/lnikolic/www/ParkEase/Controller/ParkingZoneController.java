package com.lnikolic.www.ParkEase.Controller;

import com.lnikolic.www.ParkEase.Service.ParkingZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parking_zone")
public class ParkingZoneController {

    @Autowired
    private ParkingZoneService parkingZoneService;
}
