package com.asss.www.ApotekarskaUstanova.Dto;

public class SalesItemDto {
    private int salesId;
    private Long productId;
    private String receiptType;
    private int quantity;
    private double totalPrice;

    // Konstruktori
    public SalesItemDto() {}

    public SalesItemDto(int salesId, Long productId, String receiptType, int quantity, double totalPrice) {
        this.salesId = salesId;
        this.productId = productId;
        this.receiptType = receiptType;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getteri i setteri
    public int getSalesId() { return salesId; }
    public void setSalesId(int salesId) { this.salesId = salesId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getReceiptType() { return receiptType; }
    public void setReceiptType(String receiptType) { this.receiptType = receiptType; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}

