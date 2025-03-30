package com.asss.www.ApotekarskaUstanova.Dto;

import com.asss.www.ApotekarskaUstanova.Entity.Location;

public class LocationDto {
    private int id;
    private String section;
    private String shelf;
    private String row;
    private String description;

    // Prazan konstruktor za serijalizaciju/deserijalizaciju
    public LocationDto() {
    }

    // Konstruktor koji prima Location entitet
    public LocationDto(Location location) {
        this.id = location.getLocationId();
        this.section = location.getSection();
        this.shelf = location.getShelf();
        this.row = location.getRow();
        this.description = location.getDescription();
    }

    // Getteri i setteri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}