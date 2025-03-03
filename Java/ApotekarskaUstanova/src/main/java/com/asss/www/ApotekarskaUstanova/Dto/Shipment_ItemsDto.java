package com.asss.www.ApotekarskaUstanova.Dto;

public class Shipment_ItemsDto {
    private long id;
    private String name;
    private int quantity;
    private double purchasePrice;

    public Shipment_ItemsDto() {
    }

    public Shipment_ItemsDto(long id, String name, int quantity, double purchasePrice) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    public Shipment_ItemsDto(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
}
