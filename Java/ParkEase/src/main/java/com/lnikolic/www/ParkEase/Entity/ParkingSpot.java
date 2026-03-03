package com.lnikolic.www.ParkEase.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "parking_spot")
@Data
public class ParkingSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place")
    private ParkingPlace place;

    @Column(name = "spot_floor")
    private int floor;

    @Column(name = "spot_row")
    private String row;

    @Column(name = "spot_number")
    private int number;

    @Enumerated(EnumType.STRING)
    @Column(name = "spot_type")
    private SpotType spotType;

    @Enumerated(EnumType.STRING)
    @Column(name = "spot_status")
    private SpotStatus spotStatus;

    public enum SpotType {
        REGULAR,      // Obično mesto
        DISABLED,     // Za osobe sa invaliditetom
        ELECTRIC,     //Sa punjačem
        FAMILY,       // Porodično (šire)
        VIP,          // Premium mesto
        MOTORCYCLE,   // Za motore
        TRUCK         // Za kamione
    }

    public enum SpotStatus {
        AVAILABLE,    // Slobodno
        OCCUPIED,     // Zauzeto
        RESERVED,     // Rezervisano
        MAINTENANCE,  // Na održavanju
        OUT_OF_SERVICE // Neupotrebljivo
    }
}
