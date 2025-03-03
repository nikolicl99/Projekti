package com.asss.www.ApotekarskaUstanova.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.json.JSONPropertyIgnore;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "shipments")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // Povezivanje sa Supplier entitetom
    @JoinColumn(name = "supplier_id", nullable = false)  // Kolona koja se koristi za povezivanje
    private Supplier supplier;  // Supplier objekat koji je povezan sa shipment-om

    @Column(name = "arrival_time", nullable = false)
    private Date arrivalTime;

    @OneToMany(mappedBy = "shipment")
    private List<ProductBatch> productBatches;

    public Shipment() {
    }

    public Shipment(Long id, Supplier supplier, Date arrivalTime, List<ProductBatch> productBatches) {
        this.id = id;
        this.supplier = supplier;
        this.arrivalTime = arrivalTime;
        this.productBatches = productBatches;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public List<ProductBatch> getProductBatches() {
        return productBatches;
    }

    public void setProductBatches(List<ProductBatch> productBatches) {
        this.productBatches = productBatches;
    }
}
