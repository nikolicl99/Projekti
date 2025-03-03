package com.asss.www.ApotekarskaUstanova.Dao;

import com.asss.www.ApotekarskaUstanova.Entity.Prescription_Items;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionItemsRepository extends JpaRepository<Prescription_Items, Integer> {
    List<Prescription_Items> findByPrescriptionId(Integer prescriptionId);
}
