package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Dto.ProductBatchDto;
import com.asss.www.ApotekarskaUstanova.Entity.Product;
import com.asss.www.ApotekarskaUstanova.Entity.ProductBatch;
import com.asss.www.ApotekarskaUstanova.Service.ProductBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/batches")
public class ProductBatchController {

    @Autowired
    private ProductBatchService productBatchService;

    @GetMapping("/search")
    public ResponseEntity<List<ProductBatchDto>> searchProductBatches(@RequestParam String query) {
        List<ProductBatchDto> results;
        if (query.matches("\\d+")) { // Ako su svi karakteri brojevi, pretraga po EAN13 kodu
            results = productBatchService.findByEan13(query);
        } else { // Ako je tekst, pretraga po nazivu proizvoda
            results = productBatchService.findByProductName(query);
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/product-id/{ean13}")
    public ResponseEntity<Integer> getProductIdByEan13(@PathVariable Long ean13) {
        int productId = productBatchService.getProductIdByEan13(ean13);
        return ResponseEntity.ok(productId);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductBatchDto>> getProductBatchesByProductId(@PathVariable int productId) {
        List<ProductBatchDto> batches = productBatchService.getProductBatchesByProductId(productId);
        return ResponseEntity.ok(batches);
    }

    @GetMapping("/next-batch-number/{productId}")
    public int getNextBatchNumber(@PathVariable int productId) {
        return productBatchService.getNextBatchNumberForProduct(productId);
    }

    @PostMapping("/add")
    public ResponseEntity<ProductBatchDto> addProductBatch(@RequestBody ProductBatchDto productBatchDto) {
        // Pozovite servis
        ProductBatchDto savedBatchDto = productBatchService.addProductBatch(productBatchDto);
        return ResponseEntity.ok(savedBatchDto);
    }

}
