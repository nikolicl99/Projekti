package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.Shipment_ItemsDto;
import com.asss.www.ApotekarskaUstanova.Service.ShipmentItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/shipments")
public class ShipmentItemsController {

    @Autowired
    private ShipmentItemsService shipmentItemsService;

    @GetMapping("/{shipmentId}/items")
    public ResponseEntity<List<Shipment_ItemsDto>> getShipmentItems(@PathVariable Long shipmentId) {
        List<Shipment_ItemsDto> items = shipmentItemsService.getShipmentItems(shipmentId);
        return ResponseEntity.ok(items);
    }
}
