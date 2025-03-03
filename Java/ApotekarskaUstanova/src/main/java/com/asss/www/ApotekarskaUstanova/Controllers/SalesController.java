package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.SalesDto;
import com.asss.www.ApotekarskaUstanova.Entity.Sales;
import com.asss.www.ApotekarskaUstanova.Service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/sales")
public class SalesController {

    @Autowired
    private SalesService salesService;

    @PostMapping("/add")
    public ResponseEntity<Sales> addSale(@RequestBody SalesDto salesDto) {
        Sales newSale = salesService.addSale(salesDto);
        return ResponseEntity.ok(newSale);
    }
}
