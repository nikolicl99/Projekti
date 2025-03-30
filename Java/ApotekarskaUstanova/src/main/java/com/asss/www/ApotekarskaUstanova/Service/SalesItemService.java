package com.asss.www.ApotekarskaUstanova.Service;

import com.asss.www.ApotekarskaUstanova.Repository.ProductBatchRepository;
import com.asss.www.ApotekarskaUstanova.Repository.SalesItemRepository;
import com.asss.www.ApotekarskaUstanova.Repository.SalesRepository;
import com.asss.www.ApotekarskaUstanova.Dto.ProductBatchDto;
import com.asss.www.ApotekarskaUstanova.Dto.ProductDto;
import com.asss.www.ApotekarskaUstanova.Dto.SalesItemDto;
import com.asss.www.ApotekarskaUstanova.Entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SalesItemService {

    @Autowired
    private SalesItemRepository salesItemRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private ProductBatchRepository productBatchRepository;

    public SalesItem saveSalesItem(SalesItemDto salesItemDto) {
        // Validate that productBatchId is not null or invalid
        if (salesItemDto.getProductBatchId() == 0) {
            throw new IllegalArgumentException("ProductBatch ID must not be null or invalid. Received: " + salesItemDto.getProductBatchId());
        }

        // Fetch the product batch from the repository
        ProductBatch productBatch = productBatchRepository.findById(salesItemDto.getProductBatchId())
                .orElseThrow(() -> new RuntimeException("ProductBatch ID nije pronađen: " + salesItemDto.getProductBatchId()));

        // Create and save the SalesItem
        SalesItem salesItem = new SalesItem();
        salesItem.setSales(salesRepository.findById(salesItemDto.getSalesId())
                .orElseThrow(() -> new RuntimeException("Sales ID nije pronađen: " + salesItemDto.getSalesId())));
        salesItem.setProduct(productBatch);
        salesItem.setReceiptType(salesItemDto.getReceiptType());
        salesItem.setQuantity(salesItemDto.getQuantity());
        salesItem.setTotalPrice(salesItemDto.getTotalPrice());

        SalesItem savedSalesItem = salesItemRepository.save(salesItem);
        salesItemRepository.flush(); // Force write to the database

        return savedSalesItem;
    }

    // Helper method to convert ProductBatchDto to ProductBatch entity
    private ProductBatch convertToProductBatch(ProductBatchDto productBatchDto) {
        ProductBatch productBatch = new ProductBatch();
        productBatch.setId(productBatchDto.getId());
        productBatch.setProduct(convertToProduct(productBatchDto.getProduct())); // Convert product if needed
        // Set other fields as needed
        return productBatch;
    }

    // Helper method to convert ProductDto to Product entity
    private Product convertToProduct(ProductDto productDto) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        // Set other fields as needed
        return product;
    }

    public List<SalesItem> getAllSalesItems() {
        return salesItemRepository.findAll();
    }

//    public List<SalesItemDto> getSalesItems(int salesId) {
//        List<SalesItem> items = salesItemRepository.findBySalesId(salesId);
//        return items.stream()
//                .map(item -> new SalesItemDto(item.getProduct().getProduct().getName(), item.getReceiptType(), item.getTotalPrice() ,item.getQuantity()))
//                .collect(Collectors.toList());
//    }

    public List<SalesItemDto> getSalesItems(int salesId) {
        List<SalesItem> items = salesItemRepository.findBySalesId(salesId);
        return items.stream()
                .map(item -> {
                    // Kreirajte ProductBatchDto
                    ProductBatchDto productBatchDto = new ProductBatchDto();
                    productBatchDto.setId(item.getProduct().getId());
                    productBatchDto.setBatchNumber(item.getProduct().getBatchNumber());
                    productBatchDto.setEan13(item.getProduct().getEan13());
                    productBatchDto.setProduct(convertToProductDto(item.getProduct().getProduct()));

                    // Pozovite prvi konstruktor
                    return new SalesItemDto(
                            item.getId(),
                            item.getSales().getId(),
                            productBatchDto,
                            item.getReceiptType(),
                            item.getQuantity(),
                            item.getTotalPrice()
                    );
                })
                .collect(Collectors.toList());
    }

    private ProductDto convertToProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDosage(product.getDosage());
        productDto.setDescription(product.getDescription());
        productDto.setPurchasePrice(product.getPurchasePrice());
        productDto.setSellingPrice(product.getSellingPrice());
        productDto.setStockQuantity(product.getStockQuantity());
        return productDto;
    }

    public SalesItem getSalesItemById(int id) {
        return salesItemRepository.findById(id).orElse(null);
    }
}
