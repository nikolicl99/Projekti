package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.SalesItemDto;
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
        SalesItem savedItem = salesItemService.saveSalesItem(salesItemDto);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<SalesItem>> getAllSalesItems() {
        List<SalesItem> items = salesItemService.getAllSalesItems();
        return ResponseEntity.ok(items);
    }
}
