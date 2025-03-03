package com.asss.www.ApotekarskaUstanova.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "address") // Naziv tabele u bazi podataka
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatsko generisanje ID-a
    @Column(name = "Id")
    private int id;

    @Column(name = "Address")
    private String address;

    @Column(name = "Number")
    private String number;

    @Column(name = "Apt_number")
    private String aptNumber;

    @ManyToOne // Veza sa tabelom "town" (mnogi adresi mogu biti u jednom gradu)
    @JoinColumn(name = "Town") // Kolona koja referencira grad u tabeli address
    private Town town; // Ovdje je entitet Town

    // Default konstruktor
    public Address() {}

    // Parametrizovani konstruktor
    public Address(String address, String number, String aptNumber, Town town) {
        this.address = address;
        this.number = number;
        this.aptNumber = aptNumber;
        this.town = town;
    }

    public Address(int id) {
        this.id = id;
    }

    // Getteri i setteri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAptNumber() {
        return aptNumber;
    }

    public void setAptNumber(String aptNumber) {
        this.aptNumber = aptNumber;
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }

    @Override
    public String toString() {
        return address + " " + number + (aptNumber != null ? " Apt " + aptNumber : "");
    }
}
