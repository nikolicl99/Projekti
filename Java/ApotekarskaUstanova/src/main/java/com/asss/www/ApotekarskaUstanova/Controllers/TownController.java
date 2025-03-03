package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Entity.Town;
import com.asss.www.ApotekarskaUstanova.Service.TownService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/town")
public class TownController {

    @Autowired
    private TownService townService;

    // API endpoint koji vraća gradove za zadatu opštinu
    @GetMapping("/municipality/{municipalityId}")
    public List<Town> getTownsByMunicipality(@PathVariable int municipalityId) {
        return townService.getTownsByMunicipalityId(municipalityId);
    }

    @GetMapping("/name/{townName}")
    public ResponseEntity<?> getTownId(@PathVariable String townName) {
        return townService.getTownIdByName(townName);
    }
}
