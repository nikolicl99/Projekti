package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.ShipmentDto;
import com.asss.www.ApotekarskaUstanova.Service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shipment")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @Autowired
    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping("/{supplierId}/shipments")
    public ResponseEntity<List<ShipmentDto>> getShipmentsBySupplier(@PathVariable int supplierId) {
        List<ShipmentDto> shipments = shipmentService.getShipmentsBySupplierId(supplierId);
        return ResponseEntity.ok(shipments);
    }

    @PostMapping("/add")
    public ResponseEntity<ShipmentDto> addShipment(@Valid @RequestBody ShipmentDto shipmentDto) {
        ShipmentDto newShipment = shipmentService.addShipment(shipmentDto);
        return ResponseEntity.ok(newShipment);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<ShipmentDto>> getShipmentByDate(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<ShipmentDto> shipments = shipmentService.getShipmentByDate(date);
        return ResponseEntity.ok(shipments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentDto> getShipmentById(@PathVariable int id) {
        ShipmentDto shipment = shipmentService.getShipmentById(id);
        return shipment != null ? ResponseEntity.ok(shipment) : ResponseEntity.notFound().build();
    }
}