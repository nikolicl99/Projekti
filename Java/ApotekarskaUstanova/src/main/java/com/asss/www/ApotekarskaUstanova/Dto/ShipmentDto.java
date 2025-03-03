package com.asss.www.ApotekarskaUstanova.Dto;

import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import java.util.Date;

public class ShipmentDto {
    private long id;
    private Date arrivalTime;
    private String supplierName;

    // Konstruktor koji prima Shipment objekat
    public ShipmentDto(Shipment shipment) {
        this.id = shipment.getId();
        this.arrivalTime = shipment.getArrivalTime();
        this.supplierName = shipment.getSupplier().getName();
    }

    // **NOVI KONSTRUKTOR** koji prima direktne vrednosti
    public ShipmentDto(Long id, Date arrivalTime, String supplierName) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.supplierName = supplierName;
    }

    public ShipmentDto(long id, Date arrivalTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
