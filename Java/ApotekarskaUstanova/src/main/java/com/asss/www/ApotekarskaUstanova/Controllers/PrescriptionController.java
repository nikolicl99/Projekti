package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Entity.Prescription;
import com.asss.www.ApotekarskaUstanova.Service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping("/user/{userId}")
    public List<Prescription> getPrescriptionsByUserId(@PathVariable int userId) {
        return prescriptionService.getPrescriptionsByUserId(userId);
    }

    @PutMapping("/{id}/update-status")
    public ResponseEntity<String> updatePrescriptionStatus(@PathVariable int id) {
        boolean updated = prescriptionService.updatePrescriptionStatus(id);
        if (updated) {
            return ResponseEntity.ok("Recept je uspešno ažuriran.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}