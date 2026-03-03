package com.lnikolic.www.ParkEase.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "parking_place")
@Data
public class ParkingPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone")
    private ParkingZone zone;

    @Column(name = "capacity")
    private int capacity;

    @Column(name = "address")
    private String address;
}
