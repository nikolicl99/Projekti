package com.asss.www.ApotekarskaUstanova.Dto;

import java.time.LocalDateTime;

public class SalesDto {

    private int id;
    private double totalPrice;
    private double receipt;
    private LocalDateTime transaction_date;
//    private String receipt_type;
    private long cashier_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getReceipt() {
        return receipt;
    }

    public void setReceipt(double receipt) {
        this.receipt = receipt;
    }

    public LocalDateTime getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(LocalDateTime transaction_date) {
        this.transaction_date = transaction_date;
    }

//    public String getReceipt_type() {
//        return receipt_type;
//    }
//
//    public void setReceipt_type(String receipt_type) {
//        this.receipt_type = receipt_type;
//    }

    public long getCashier_id() {
        return cashier_id;
    }

    public void setCashier_id(long cashier_id) {
        this.cashier_id = cashier_id;
    }
}
