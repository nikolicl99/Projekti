package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.TownRepository;
import com.asss.www.ApotekarskaUstanova.Entity.Town;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TownService {

    @Autowired
    private TownRepository townRepository;

    // Metoda koja vraća sve gradove za zadatu opštinu
    public List<Town> getTownsByMunicipalityId(int municipalityId) {
        return townRepository.findByMunicipalityId(municipalityId);
    }

    public ResponseEntity<?> getTownIdByName(String townName) {
        Optional<Town> town = townRepository.findByName(townName);
        if (town.isPresent()) {
            // Ako grad postoji, vraćamo id u JSON formatu
            return ResponseEntity.ok().body("{\"id\": " + town.get().getId() + "}");
        } else {
            // Ako grad nije pronađen, vraćamo 404 sa odgovarajućim JSON odgovorom
            return ResponseEntity.status(404).body("{\"message\": \"Grad nije pronađen\"}");
        }
    }
}
