package com.asss.www.ApotekarskaUstanova.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.json.JSONPropertyIgnore;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "shipments")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)  // Povezivanje sa Supplier entitetom
    @JoinColumn(name = "supplier_id", nullable = false)  // Kolona koja se koristi za povezivanje
    private Supplier supplier;  // Supplier objekat koji je povezan sa shipment-om

    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;

    @Column(name = "arrival_time", nullable = false)
    private Time arrivalTime;


    public Shipment() {
    }

    public Shipment(int id, Supplier supplier, LocalDate arrivalDate, Time arrivalTime) {
        this.id = id;
        this.supplier = supplier;
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
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
}
