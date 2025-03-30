package com.asss.www.ApotekarskaUstanova.Dto;

import com.asss.www.ApotekarskaUstanova.Entity.Municipality;

public class MunicipalityDto {
    private int id;
    private String name;

    // Prazan konstruktor za serijalizaciju/deserijalizaciju
    public MunicipalityDto() {
    }

    // Konstruktor koji prima Municipality entitet
    public MunicipalityDto(Municipality municipality) {
        if (municipality == null) {
            throw new IllegalArgumentException("Municipality ne može biti null.");
        }

        this.id = municipality.getId();
        this.name = municipality.getName();
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
}