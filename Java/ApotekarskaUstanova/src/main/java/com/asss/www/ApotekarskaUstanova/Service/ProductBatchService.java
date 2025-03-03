package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Dao.ProductBatchRepository;
import com.asss.www.ApotekarskaUstanova.Dao.ProductRepository;
import com.asss.www.ApotekarskaUstanova.Dao.SupplierRepository;
import com.asss.www.ApotekarskaUstanova.Dto.ProductBatchDto;
import com.asss.www.ApotekarskaUstanova.Entity.Product;
import com.asss.www.ApotekarskaUstanova.Entity.ProductBatch;
import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import com.asss.www.ApotekarskaUstanova.Entity.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductBatchService {

    private final ProductBatchRepository productBatchRepository;
    private final SupplierRepository supplierRepository;  // Dodavanje SupplierRepository-a
    private ProductRepository productRepository;

    @Autowired
    public ProductBatchService(ProductBatchRepository productBatchRepository, SupplierRepository supplierRepository) {
        this.productBatchRepository = productBatchRepository;
        this.supplierRepository = supplierRepository;
    }

    public List<ProductBatch> getAllBatches() {
        return productBatchRepository.findAll();
    }

    public Optional<ProductBatch> getBatchById(Long id) {
        return productBatchRepository.findById(id);
    }

//    public Long getBatchIdByEan13(Long ean13) {
//        return productBatchRepository.findByEan13(ean13)
//                .map(ProductBatch::getId)
//                .orElseThrow(() -> new RuntimeException("Batch ID nije pronađen za EAN: " + ean13));
//    }

    // Pronalazi Product ID na osnovu Batch ID-a
    public Long getProductIdByBatchId(Long batchId) {
        return productBatchRepository.findById(batchId)
                .map(ProductBatch::getProduct)
                .orElseThrow(() -> new RuntimeException("Product ID nije pronađen za Batch ID: " + batchId)).getId();
    }

    // Pronalazi informacije o proizvodu koristeći ProductsService


    public ProductBatch saveBatch(ProductBatchDto productBatchDto) {
        // Konvertovanje DTO u entitet pre nego što ga sačuvate
        ProductBatch productBatch = convertToEntity(productBatchDto);
        return productBatchRepository.save(productBatch);
    }

    public void deleteBatch(Long id) {
        productBatchRepository.deleteById(id);
    }

    public List<ProductBatchDto> getAllProductBatches() {
        List<ProductBatch> productBatches = productBatchRepository.findAll();
        return productBatches.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductBatch getBatchByEan13(Long ean13) {
        return productBatchRepository.findByEan13(ean13)
                .orElseThrow(() -> new RuntimeException("Batch sa EAN13 kodom " + ean13 + " nije pronađen."));
    }

    // Dohvatanje product_id na osnovu EAN13
    public Long getProductIdByEan13(Long ean13) {
        ProductBatch batch = getBatchByEan13(ean13);
        return batch.getProduct().getId(); // Preuzimamo ID proizvoda
    }

    public List<ProductBatchDto> getProductBatchesByProductId(Long productId) {
        return productBatchRepository.findByProductId(productId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

//    public Long getBatchIdByEan13(String ean13) {
//        Long ean13Long = Long.parseLong(ean13); // Konvertuje String u Long
//        Optional<ProductBatch> optionalBatch = productBatchRepository.findByEan13(ean13Long);
//        return optionalBatch.map(ProductBatch::getId).orElse(null); // Vraća ID ili null ako batch nije pronađen
//    }

    public List<ProductBatchDto> findByEan13(String ean13) {
        return productBatchRepository.findByEan13(ean13).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    public List<ProductBatchDto> findByProductName(String name) {
        return productBatchRepository.findByProductName(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    private ProductBatchDto convertToDto(ProductBatch productBatch) {
        ProductBatchDto dto = new ProductBatchDto();
        dto.setId(productBatch.getId());
        dto.setBatchNumber(productBatch.getBatchNumber());
        dto.setEan13(productBatch.getEan13());
        dto.setReceivedDate(productBatch.getShipment().getArrivalTime());
        dto.setExpirationDate(productBatch.getExpirationDate());
        dto.setQuantityReceived(productBatch.getQuantityReceived());
        dto.setQuantityRemaining(productBatch.getQuantityRemaining());
        dto.setSupplierName(productBatch.getShipment().getSupplier().getName());

        // Postavljamo naziv proizvoda ako postoji
        if (productBatch.getProduct() != null) {
            dto.setProductName(productBatch.getProduct().getName());
        } else {
            dto.setProductName("Nepoznato");
        }

        return dto;
    }


    private ProductBatch convertToEntity(ProductBatchDto productBatchDto) {
        ProductBatch productBatch = new ProductBatch();
        productBatch.setBatchNumber(productBatchDto.getBatchNumber());
        productBatch.setExpirationDate(productBatchDto.getExpirationDate());
        productBatch.setQuantityReceived(productBatchDto.getQuantityReceived());
        productBatch.setQuantityRemaining(productBatchDto.getQuantityRemaining());

        // Ako želite da postavite "receivedDate" u ProductBatch, morate da ga dodate u ProductBatch entitet.
        // Pretpostavljam da želite da ga postavite iz Shipment entiteta
        if (productBatchDto.getReceivedDate() != null) {
            Shipment shipment = new Shipment();
            shipment.setArrivalTime(productBatchDto.getReceivedDate());
            productBatch.setShipment(shipment);
        }

        // Pronalaženje dobavljača na osnovu imena i povezivanje sa Shipment entitetom
        Optional<Supplier> supplierOpt = supplierRepository.findByName(productBatchDto.getSupplierName());
        if (supplierOpt.isPresent()) {
            Supplier supplier = supplierOpt.get();
            Shipment shipment = productBatch.getShipment();  // Dobijanje Shipment objekta
            if (shipment == null) {
                shipment = new Shipment();  // Ako ne postoji, kreiraj novi Shipment objekat
            }
            shipment.setSupplier(supplier);  // Povezivanje dobavljača sa Shipment
            productBatch.setShipment(shipment);  // Povezivanje Shipment sa ProductBatch
        }

        return productBatch;
    }

}
