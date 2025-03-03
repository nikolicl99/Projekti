package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.ShipmentDto;
import com.asss.www.ApotekarskaUstanova.Entity.Address;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import com.asss.www.ApotekarskaUstanova.Entity.Supplier;
import com.asss.www.ApotekarskaUstanova.Service.SuppliersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("api/suppliers")
public class SuppliersController {

    @Autowired
    private SuppliersService suppliersService;

    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return suppliersService.getSuppliers();
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
    public ResponseEntity<Map<String, Integer>> getSupplierId(@PathVariable String name) {
        int supplierId = suppliersService.getSupplierIdByName(name);

        if (supplierId != -1) {
            return ResponseEntity.ok(Collections.singletonMap("id", supplierId));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", -1));
        }
    }

    // Novi endpoint za dohvatanje poslednjih 4-5 isporuka dobavljača
    @GetMapping("/{id}/recent-shipments")
    public ResponseEntity<List<ShipmentDto>> getRecentShipments(@PathVariable int id) {
        List<Shipment> shipments = suppliersService.getRecentShipments(id);

        if (shipments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        // Mapiranje entiteta u DTO kako bi izbegli LAZY problema
        List<ShipmentDto> shipmentDtos = shipments.stream()
                .map(shipment -> new ShipmentDto(
                        shipment.getId(),
                        shipment.getArrivalTime(),
                        shipment.getSupplier().getName() // Izbegavanje LAZY problema
                ))
                .toList();

        return ResponseEntity.ok(shipmentDtos);
    }

}
