package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.SalesItemDto;
import com.asss.www.ApotekarskaUstanova.Dto.Shipment_ItemsDto;
import com.asss.www.ApotekarskaUstanova.Entity.Sales;
import com.asss.www.ApotekarskaUstanova.Entity.SalesItem;
import com.asss.www.ApotekarskaUstanova.Service.SalesItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales_items")
public class SalesItemController {

    @Autowired
    private SalesItemService salesItemService;

    @PostMapping("/add")
    public ResponseEntity<SalesItem> addSalesItem(@RequestBody SalesItemDto salesItemDto) {
        // Log the incoming SalesItemDto object
        System.out.println("Received SalesItemDto: " + salesItemDto);
        System.out.println("ProductBatchId: " + salesItemDto.getProductBatchId());

        SalesItem newSalesItem = salesItemService.saveSalesItem(salesItemDto);
        return ResponseEntity.ok(newSalesItem);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SalesItem>> getAllSalesItems() {
        List<SalesItem> items = salesItemService.getAllSalesItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesItem> getSaleById(@PathVariable int id) {
        SalesItem salesItem = salesItemService.getSalesItemById(id);
        return salesItem != null ? ResponseEntity.ok(salesItem) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{salesId}/items")
    public ResponseEntity<List<SalesItemDto>> getSalesItems(@PathVariable int salesId) {
        List<SalesItemDto> items = salesItemService.getSalesItems(salesId);
        return ResponseEntity.ok(items);
    }
}
