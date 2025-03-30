package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.ShipmentDto;
import com.asss.www.ApotekarskaUstanova.Dto.SupplierDto;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import com.asss.www.ApotekarskaUstanova.Entity.Supplier;
import com.asss.www.ApotekarskaUstanova.Service.SuppliersService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/suppliers")
public class SuppliersController {

    @Autowired
    private SuppliersService suppliersService;

    @GetMapping
    public ResponseEntity<List<SupplierDto>> getAllSuppliers() {
        List<SupplierDto> suppliers = suppliersService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addSupplier(@RequestBody @Valid Supplier supplier) {
        System.out.println("Primljena adresa: " + supplier);

        Supplier savedSupplier = suppliersService.addSupplier(supplier);
        if (savedSupplier != null && savedSupplier.getId() != -1) {
            return new ResponseEntity<>("Supplier successfully added with ID: " + savedSupplier.getId(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Supplier could not be added", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSuppliers(@PathVariable int id) {
        Supplier supplier = suppliersService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Integer> getSupplierId(@PathVariable String name) {
        Integer supplierId = suppliersService.getSupplierIdByName(name);

        if (supplierId != null) {
            return ResponseEntity.ok(supplierId); // Return the supplierId directly
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(-1); // Return -1 if not found
        }
    }

    @GetMapping("/{supplierId}/shipments")
    public ResponseEntity<?> getAllShipmentsBySupplierId(@PathVariable int supplierId) {
        try {
            List<ShipmentDto> shipments = suppliersService.getAllShipmentsBySupplierId(supplierId);
            return ResponseEntity.ok(shipments);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Endpoint za dohvatanje poslednjih 5 isporuka za određenog dobavljača
    @GetMapping("/{supplierId}/recent-shipments")
    public ResponseEntity<?> getRecentShipments(@PathVariable int supplierId) {
        try {
            List<ShipmentDto> shipments = suppliersService.getRecentShipments(supplierId);
            return ResponseEntity.ok(shipments);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}