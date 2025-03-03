package com.asss.www.ApotekarskaUstanova.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "town") // Naziv tabele u bazi podataka
public class Town {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatsko generisanje ID-a
    @Column(name = "Id")
    private int id;

    @Column(name = "Name")
    private String name;

    @ManyToOne // Veza sa tabelom "municipality" (mnogi gradovi mogu biti u jednoj opštini)
    @JoinColumn(name = "municipality") // Kolona koja referencira opštinu u tabeli town
    private Municipality municipality;

    @Column(name = "Postal_Code")
    private int postalCode;

    // Default konstruktor
    public Town() {}

    public Town(int id, String name, Municipality municipality, int postalCode) {
        this.id = id;
        this.name = name;
        this.municipality = municipality;
        this.postalCode = postalCode;
    }

    // Getteri i setteri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Municipality getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public String toString() {
        return name;
    }
}
