package com.lnikolic.www.ParkEase.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "parking_zone")
@Data
public class ParkingZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "hourly_rate")
    private int hourlyRate;


}
