package com.asss.www.ApotekarskaUstanova.Dto;

public class Shipment_ItemsDto {
    private int id;
    private int shipment_id;
    private ShipmentDto shipment;
    private int product_id;
    private ProductBatchDto productBatch;
    private int quantity;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getteri i setteri
    public int getShipment_id() {
        return shipment_id;
    }

    public void setShipment_id(int shipment_id) {
        this.shipment_id = shipment_id;
    }

    public ShipmentDto getShipment() {
        return shipment;
    }

    public void setShipment(ShipmentDto shipment) {
        this.shipment = shipment;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public ProductBatchDto getProductBatch() {
        return productBatch;
    }

    public void setProductBatch(ProductBatchDto productBatch) {
        this.productBatch = productBatch;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}