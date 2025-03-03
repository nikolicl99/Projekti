package com.asss.www.ApotekarskaUstanova.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ProductBatchDto {
    private long id;
    private String batchNumber;
    private Long ean13;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date receivedDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date expirationDate;

    private int quantityReceived;
    private int quantityRemaining;
    private String supplierName;

    // Novo polje za naziv proizvoda
    private String productName;

    public ProductBatchDto() {
    }

    public ProductBatchDto(Long id, String name, Date expirationDate, Integer quantityRemaining, Long ean13) {
    }

    // Getteri i setteri
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Long getEan13() {
        return ean13;
    }

    public void setEan13(Long ean13) {
        this.ean13 = ean13;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(int quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public int getQuantityRemaining() {
        return quantityRemaining;
    }

    public void setQuantityRemaining(int quantityRemaining) {
        this.quantityRemaining = quantityRemaining;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    // Getter i setter za productName
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
