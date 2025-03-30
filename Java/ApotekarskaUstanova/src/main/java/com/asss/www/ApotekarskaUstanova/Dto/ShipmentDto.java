package com.asss.www.ApotekarskaUstanova.Dto;

import com.asss.www.ApotekarskaUstanova.Entity.Shipment;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Time;
import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) klasa za Shipment entitet.
 * Koristi se za prenos podataka o isporukama između slojeva aplikacije.
 */
public class ShipmentDto {
    private int id; // ID isporuke
    @JsonProperty("supplier_id")
    private int supplierId;
    @JsonProperty("arrivalDate") // Mapira JSON polje "arrivalDate" na arrival_date
    private LocalDate arrivalDate;
    @JsonProperty("arrivalTime") // Mapira JSON polje "arrivalTime" na arrival_time
    private Time arrivalTime;
    private String supplierName; // Ime dobavljača
    private SupplierDto supplier; // DTO objekat za dobavljača

    // **Konstruktor za konverziju iz Shipment entiteta**
    public ShipmentDto(Shipment shipment) {
        this.id = shipment.getId();
        this.arrivalDate = shipment.getArrivalDate();
        this.arrivalTime = shipment.getArrivalTime();
        this.supplierName = shipment.getSupplier().getName();
        this.supplierId = shipment.getSupplier().getId();
        this.supplier = new SupplierDto(shipment.getSupplier()); // Pretpostavimo da postoji odgovarajući konstruktor u SupplierDto
    }

    // **Konstruktor za direktno postavljanje vrednosti**
    public ShipmentDto(int id, LocalDate arrivalDate, Time arrivalTime, String supplierName, int supplierId, SupplierDto supplier) {
        this.id = id;
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.supplierName = supplierName;
        this.supplierId = supplierId;
        this.supplier = supplier;
    }

    // **Konstruktor za minimalne podatke**
    public ShipmentDto(int id, LocalDate arrivalDate, Time arrivalTime) {
        this.id = id;
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
    }

    // **Prazan konstruktor za serijalizaciju/deserijalizaciju**
    public ShipmentDto() {
    }

    public ShipmentDto(int id, Time arrivalTime, String name) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.supplierName = name;
    }

    public ShipmentDto(int id, LocalDate arrivalDate, Time arrivalTime, String supplierName) {
        this.id = id;
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.supplierName = supplierName;
    }

    // Getteri i Setteri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Time arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public SupplierDto getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierDto supplier) {
        this.supplier = supplier;
    }
}