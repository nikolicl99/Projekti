package com.lnikolic.www.ParkEase.Controller;

import com.lnikolic.www.ParkEase.Service.ParkingPlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parking_place")
public class ParkingPlaceController {

    @Autowired
    private ParkingPlaceService parkingPlaceService;
}
