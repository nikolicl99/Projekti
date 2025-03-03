package com.asss.www.ApotekarskaUstanova.Dao;


import com.asss.www.ApotekarskaUstanova.Entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {
    List<Prescription> findByUserId(Integer userId);
}

