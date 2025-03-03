package com.asss.www.ApotekarskaUstanova.Dao;

import com.asss.www.ApotekarskaUstanova.Entity.Town;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TownRepository extends JpaRepository<Town, Integer> {
    // Metoda za pronalaženje svih gradova prema ID-u opštine
    List<Town> findByMunicipalityId(int municipalityId);
    Optional<Town> findByName(String name);
}