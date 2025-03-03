package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Entity.Prescription_Items;
import com.asss.www.ApotekarskaUstanova.Service.PrescriptionItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionItemsController {

    @Autowired
    private PrescriptionItemsService prescriptionItemService;

    @GetMapping("/{prescriptionId}/items")
    public ResponseEntity<List<Prescription_Items>> getPrescriptionItems(@PathVariable int prescriptionId) {
        List<Prescription_Items> items = prescriptionItemService.getItemsByPrescriptionId(prescriptionId);
        if (items.isEmpty()) {
            return ResponseEntity.noContent().build(); // Vraća 204 ako nema stavki
        }
        return ResponseEntity.ok(items); // Vraća 200 i listu stavki
    }
}
