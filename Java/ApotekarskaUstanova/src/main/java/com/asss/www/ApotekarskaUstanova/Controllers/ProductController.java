package com.asss.www.ApotekarskaUstanova.Controllers;

import com.asss.www.ApotekarskaUstanova.Repository.ProductBatchRepository;
import com.asss.www.ApotekarskaUstanova.Repository.ProductRepository;
import com.asss.www.ApotekarskaUstanova.Dto.ProductBatchDto;
import com.asss.www.ApotekarskaUstanova.Dto.ProductDto;
import com.asss.www.ApotekarskaUstanova.Dto.ShipmentDto;
import com.asss.www.ApotekarskaUstanova.Dto.SupplierDto;
import com.asss.www.ApotekarskaUstanova.Entity.Product;
import com.asss.www.ApotekarskaUstanova.Entity.ProductBatch;
import com.asss.www.ApotekarskaUstanova.Service.ProductBatchService;
import com.asss.www.ApotekarskaUstanova.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductBatchService productBatchService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductBatchRepository productBatchRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    public ProductController(ProductBatchService productBatchService) {
        this.productBatchService = productBatchService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/name/{productName}")
    public ResponseEntity<Integer> getProductId(@PathVariable String productName) {
        int productId = productService.getProductIdByName(productName);
        if (productId != -1) {
            return ResponseEntity.ok(productId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/batches")
    public List<ProductBatchDto> getAllProductBatches() {
        return productBatchService.getAllProductBatches();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        ProductDto productDto = convertToDto(product); // Pretpostavi da imaš metodu za konverziju
        return ResponseEntity.ok(productDto);
    }

    @GetMapping("/by-ean13/{ean13}")
    public ResponseEntity<?> getProductByEan13(@PathVariable String ean13) {
        Optional<Product> product = productService.findProductByEan13(ean13);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Product())); // Return an empty Product object
    }


    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSellingPrice(product.getSellingPrice());
        // Dodaj ostala polja prema potrebi
        return dto;
    }

    @GetMapping("/dosages")
    public ResponseEntity<List<Integer>> getProductDosages(@RequestParam String name) {
        List<Integer> dosages = productService.getProductDosages(name);
        return ResponseEntity.ok(dosages);
    }

    @PostMapping("/batches")
    public ResponseEntity<ProductBatchDto> createBatch(@RequestBody ProductBatchDto productBatchDto) {
        ProductBatch savedBatch = productBatchService.saveBatch(productBatchDto);
        ProductBatchDto savedBatchDto = convertToDto(savedBatch);
        return ResponseEntity.ok(savedBatchDto);
    }

    @PutMapping("/batches/{id}")
    public ResponseEntity<ProductBatchDto> updateBatch(@PathVariable int id, @RequestBody ProductBatchDto productBatchDto) {
        return productBatchService.getBatchById(id).map(existingBatch -> {
            productBatchDto.setId(id);
            ProductBatch updatedBatch = productBatchService.saveBatch(productBatchDto);
            ProductBatchDto updatedBatchDto = convertToDto(updatedBatch);
            return ResponseEntity.ok(updatedBatchDto);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/batches/{id}")
    public ResponseEntity<Void> deleteBatch(@PathVariable int id) {
        if (productBatchService.getBatchById(id).isPresent()) {
            productBatchService.deleteBatch(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Helper metoda za konvertovanje entiteta u DTO
    private ProductBatchDto convertToDto(ProductBatch productBatch) {
        ProductBatchDto productBatchDto = new ProductBatchDto();

        productBatchDto.setId(productBatch.getId());
        productBatchDto.setBatchNumber(productBatch.getBatchNumber());
        productBatchDto.setExpirationDate(productBatch.getExpirationDate());
        productBatchDto.setQuantityReceived(productBatch.getQuantityReceived());
        productBatchDto.setQuantityRemaining(productBatch.getQuantityRemaining());

        // Uzmi podatke iz Shipment entiteta
        if (productBatch.getShipment() != null) {
            // Kreiranje ShipmentDto
            ShipmentDto shipmentDto = new ShipmentDto();

            // Postavljanje osnovnih podataka o isporuci
            shipmentDto.setId(productBatch.getShipment().getId());
            shipmentDto.setArrivalDate(productBatch.getShipment().getArrivalDate());
            shipmentDto.setArrivalTime(productBatch.getShipment().getArrivalTime());

            // Postavljanje podataka o dobavljaču u ShipmentDto
            if (productBatch.getShipment().getSupplier() != null) {
                // Kreiranje SupplierDto
                SupplierDto supplierDto = new SupplierDto();
                supplierDto.setId(productBatch.getShipment().getSupplier().getId());
                supplierDto.setName(productBatch.getShipment().getSupplier().getName());

                // Postavljanje SupplierDto u ShipmentDto
                shipmentDto.setSupplier(supplierDto);
                shipmentDto.setSupplierName(productBatch.getShipment().getSupplier().getName());
            }

            // Postavljanje ShipmentDto u ProductBatchDto
            productBatchDto.setShipmentDto(shipmentDto);


            // Postavljanje datuma prijema iz Shipment entiteta
//            productBatchDto.setReceivedDate(productBatch.getShipment().getArrivalDate());
        }

        return productBatchDto;
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProductBatches(@RequestParam String query) {
        List<ProductDto> results = productService.findByName(query);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDto>> getLowStockProducts() {
        List<ProductDto> lowStockProducts = productService.getLowStockProducts();
        return ResponseEntity.ok(lowStockProducts);
    }

}
