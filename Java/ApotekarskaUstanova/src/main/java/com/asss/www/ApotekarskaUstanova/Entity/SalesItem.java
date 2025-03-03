package com.asss.www.ApotekarskaUstanova.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sales_items")
public class SalesItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sales_id", nullable = false)
    private Sales sales;

    @Column(name = "product_id", nullable = false)
    private Long productId; // Veza sa product_batches

    @Column(name = "receipt_type")
    private String receiptType;

    @Column(name = "prescriptionItem", nullable = false)
    private int prescriptionItem;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Sales getSales() {
        return sales;
    }

    public void setSales(Sales sales) {
        this.sales = sales;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getReceiptType() {
        return receiptType;
    }

    public void setReceiptType(String receiptType) {
        this.receiptType = receiptType;
    }

    public int getPrescriptionItem() {
        return prescriptionItem;
    }

    public void setPrescriptionItem(int prescriptionItem) {
        this.prescriptionItem = prescriptionItem;
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
