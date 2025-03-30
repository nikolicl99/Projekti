package com.asss.www.ApotekarskaUstanova.Dto;

import com.asss.www.ApotekarskaUstanova.Entity.Address;

public class AddressDto {
    private int id;
    private String address;
    private String number;
    private String aptNumber;
    private int town_id;
    private TownDto town;

    // Prazan konstruktor za serijalizaciju/deserijalizaciju
    public AddressDto() {
    }

    public AddressDto(int id) {
        this.id = id;
    }


    // Konstruktor koji prima Address entitet
    public AddressDto(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address ne može biti null.");
        }

        this.id = address.getId();
        this.address = address.getAddress();
        this.number = address.getNumber();
        this.aptNumber = address.getAptNumber();
        this.town_id = (address.getTown() != null) ? address.getTown().getId() : 0;
        this.town = (address.getTown() != null) ? new TownDto(address.getTown()) : null;
    }

    public AddressDto(int id, String address, String number, String aptNumber) {
        this.id = id;
        this.address = address;
        this.number = number;
        this.aptNumber = aptNumber;
    }

    public AddressDto(int id, String address, String number, String aptNumber, int town_id, TownDto town) {
        this.id = id;
        this.address = address;
        this.number = number;
        this.aptNumber = aptNumber;
        this.town_id = town_id;
        this.town = town;
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

    public int getTown_id() {
        return town_id;
    }

    public void setTown_id(int town_id) {
        this.town_id = town_id;
    }

    public TownDto getTown() {
        return town;
    }

    public void setTown(TownDto town) {
        this.town = town;
    }
}