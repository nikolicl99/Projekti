package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.Shipment_ItemsDto;
import com.asss.www.ApotekarskaUstanova.Service.ShipmentItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/shipment-items")
public class ShipmentItemsController {

    @Autowired
    private ShipmentItemsService shipmentItemsService;

    @GetMapping("/{shipmentId}/items")
    public ResponseEntity<List<Shipment_ItemsDto>> getShipmentItems(@PathVariable Long shipmentId) {
        List<Shipment_ItemsDto> items = shipmentItemsService.getShipmentItems(shipmentId);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/add")
    public ResponseEntity<Shipment_ItemsDto> addShipmentItem(@RequestBody Shipment_ItemsDto shipmentItemsDto) {
        Shipment_ItemsDto itemsDto = shipmentItemsService.addShipmentItem(shipmentItemsDto);
        return ResponseEntity.ok(itemsDto);
    }
}
