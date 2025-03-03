package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Entity.Municipality;
import com.asss.www.ApotekarskaUstanova.Service.MunicipalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/municipality")
public class MunicipalityController {

    @Autowired
    private MunicipalityService municipalityService;

    @GetMapping
    public List<Municipality> getAllMunicipalities() {
        return municipalityService.getAllMunicipalities(); // Vraća listu svih opština
    }

    @GetMapping("/name/{municipalityName}")
    public ResponseEntity<Map<String, Integer>> getMunicipalityId(@PathVariable String municipalityName) {
        Integer municipalityId = municipalityService.getMunicipalityIdByName(municipalityName);

        if (municipalityId != null) {
            // Ako opština postoji, vraćamo ID u JSON formatu
            Map<String, Integer> response = new HashMap<>();
            response.put("id", municipalityId);
            return ResponseEntity.ok(response);
        } else {
            // Ako opština nije pronađena, vraćamo HTTP 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
