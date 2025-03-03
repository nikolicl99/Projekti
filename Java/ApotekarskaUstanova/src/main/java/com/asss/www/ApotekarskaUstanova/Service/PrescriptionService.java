package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.PrescriptionRepository;
import com.asss.www.ApotekarskaUstanova.Entity.Prescription;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public List<Prescription> getPrescriptionsByUserId(int userId) {
        return prescriptionRepository.findByUserId(userId);
    }

    @Transactional
    public boolean updatePrescriptionStatus(int id) {
        Optional<Prescription> prescription = prescriptionRepository.findById(id);
        if (prescription.isPresent()) {
            Prescription p = prescription.get();
            p.setObtained(true); // Postavi obtained na true
            prescriptionRepository.save(p);
            return true;
        }
        return false;
    }
}
