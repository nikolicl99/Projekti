package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.MunicipalityRepository;
import com.asss.www.ApotekarskaUstanova.Entity.Municipality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MunicipalityService {

    @Autowired
    private MunicipalityRepository municipalityRepository;

    public List<Municipality> getAllMunicipalities() {
        return municipalityRepository.findAll(); // Vraća sve opštine iz baze
    }

    public Integer getMunicipalityIdByName(String municipalityName) {
        Municipality municipality = municipalityRepository.findByName(municipalityName);
        if (municipality != null) {
            return municipality.getId(); // Vraća ID opštine
        }
        return null; // Ako nije pronađena, vraća null
    }
}
