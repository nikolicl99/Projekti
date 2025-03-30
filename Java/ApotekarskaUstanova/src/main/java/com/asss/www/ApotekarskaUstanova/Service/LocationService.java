package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Entity.Location;
import com.asss.www.ApotekarskaUstanova.Repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    // Metoda za dobijanje svih lokacija
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public int findLocationId(String section, String shelf, String row, String description) {
        Optional<Location> locationOpt = locationRepository.findBySectionAndShelfAndRowAndDescription(section, shelf, row, description);
        if (locationOpt.isPresent()) {
            return locationOpt.get().getLocationId();
        } else {
            throw new RuntimeException("Lokacija nije pronađena za unete parametre.");
        }
    }
}