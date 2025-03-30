package com.asss.www.ApotekarskaUstanova.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SalesItemDto {
    private int id;
    private int salesId;

    @JsonProperty("product_batch_id") // JSON field for the product batch ID
    private int productBatchId;

    private ProductBatchDto productBatch; // Use ProductBatchDto instead of int
    private String receiptType;
    private int quantity;
    private double totalPrice;

    // Constructors
    public SalesItemDto() {}

    public SalesItemDto(int id, int salesId, int productBatchId, String receiptType, int quantity, double totalPrice) {
        this.id = id;
        this.salesId = salesId;
        this.productBatchId = productBatchId;
        this.receiptType = receiptType;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Constructor with ProductBatchDto
    public SalesItemDto(int id, int salesId, ProductBatchDto productBatch, String receiptType, int quantity, double totalPrice) {
        this.id = id;
        this.salesId = salesId;
        this.productBatch = productBatch;
        this.receiptType = receiptType;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSalesId() {
        return salesId;
    }

    public void setSalesId(int salesId) {
        this.salesId = salesId;
    }

    public int getProductBatchId() {
        return productBatchId;
    }

    public void setProductBatchId(int productBatchId) {
        this.productBatchId = productBatchId;
    }

    public ProductBatchDto getProductBatch() {
        return productBatch;
    }

    public void setProductBatch(ProductBatchDto productBatch) {
        this.productBatch = productBatch;
    }

    public String getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(String receiptType) {
        this.receiptType = receiptType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}