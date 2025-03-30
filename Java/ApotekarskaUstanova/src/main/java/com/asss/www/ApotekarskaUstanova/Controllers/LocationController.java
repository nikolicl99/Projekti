package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Entity.Location;
import com.asss.www.ApotekarskaUstanova.Service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    // Endpoint za dobijanje svih lokacija
    @GetMapping
    public List<Location> getAllLocations() {
        return locationService.getAllLocations();
    }

    @GetMapping("/find")
    public ResponseEntity<Integer> findLocationId(
            @RequestParam String section,
            @RequestParam String shelf,
            @RequestParam String row,
            @RequestParam String description) {
        int locationId = locationService.findLocationId(section, shelf, row, description);
        return ResponseEntity.ok(locationId);
    }
}