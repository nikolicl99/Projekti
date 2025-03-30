package com.asss.www.ApotekarskaUstanova.Dto;

import com.asss.www.ApotekarskaUstanova.Entity.ProductBatch;

import java.time.LocalDate;

public class ProductBatchDto {
    private int id;
    private String batchNumber;
    private long ean13;
    private LocalDate expirationDate;  // Promenjeno na LocalDate
    private int quantityReceived;
    private int quantityRemaining;
    private int product_id;
    private ProductDto product;
    private int shipment_id;
    private ShipmentDto shipmentDto;
    private int location_id;
    private LocationDto locationDto;

    public ProductBatchDto(int product_id, long ean13, String batchNumber, LocalDate expirationDate,
                           int quantityReceived, int quantityRemaining, int shipment_id, int location_id) {
        this.product_id = product_id;
        this.ean13 = ean13;
        this.batchNumber = batchNumber;
        this.expirationDate = expirationDate;
        this.quantityReceived = quantityReceived;
        this.quantityRemaining = quantityRemaining;
        this.shipment_id = shipment_id;
        this.location_id = location_id;
    }

    public ProductBatchDto(ProductBatch productBatch) {
        this.id = productBatch.getId();
        this.batchNumber = productBatch.getBatchNumber();
        this.ean13 = productBatch.getEan13();
        this.expirationDate = productBatch.getExpirationDate();
        this.quantityReceived = productBatch.getQuantityReceived();
        this.quantityRemaining = productBatch.getQuantityRemaining();
        this.product_id = productBatch.getProduct().getId();
        this.product = new ProductDto(productBatch.getProduct());
    }

    public ProductBatchDto() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public long getEan13() {
        return ean13;
    }

    public void setEan13(long ean13) {
        this.ean13 = ean13;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
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

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public int getShipment_id() {
        return shipment_id;
    }

    public void setShipment_id(int shipment_id) {
        this.shipment_id = shipment_id;
    }

    public ShipmentDto getShipmentDto() {
        return shipmentDto;
    }

    public void setShipmentDto(ShipmentDto shipmentDto) {
        this.shipmentDto = shipmentDto;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public LocationDto getLocationDto() {
        return locationDto;
    }

    public void setLocationDto(LocationDto locationDto) {
        this.locationDto = locationDto;
    }
}
