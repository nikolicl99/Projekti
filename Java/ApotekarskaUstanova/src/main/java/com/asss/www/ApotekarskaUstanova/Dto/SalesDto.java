package com.asss.www.ApotekarskaUstanova.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;

public class SalesDto {

    private int id;
    private double totalPrice;

    @JsonProperty("change")
    private double receiptChange;

    @JsonProperty("transactionDate")
    private LocalDate transactionDate;

    @JsonProperty("transactionTime")
    private Time transactionTime;

//    @JsonProperty("payment_type")
    private String paymentType;


    private int employeeId; // For JSON deserialization

//    @JsonIgnore // This field will not be serialized/deserialized
@JsonProperty("cashier")
    private EmployeeDto employeeDto; // For business logic

    // Constructors, getters, and setters

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

    public double getReceiptChange() {
        return receiptChange;
    }

    public void setReceiptChange(double receiptChange) {
        this.receiptChange = receiptChange;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Time getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Time transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public EmployeeDto getEmployeeDto() {
        return employeeDto;
    }

    public void setEmployeeDto(EmployeeDto employeeDto) {
        this.employeeDto = employeeDto;
    }
}