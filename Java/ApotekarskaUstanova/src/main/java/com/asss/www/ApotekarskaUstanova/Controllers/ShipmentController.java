package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.ShipmentDto;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import com.asss.www.ApotekarskaUstanova.Service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @Autowired
    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping("/{supplierId}/shipments")
    public ResponseEntity<List<ShipmentDto>> getShipmentsBySupplier(@PathVariable Long supplierId) {
        List<ShipmentDto> shipments = shipmentService.getShipmentsBySupplier(supplierId);
        return ResponseEntity.ok(shipments);
    }
}
