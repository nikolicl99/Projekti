package com.asss.www.ApotekarskaUstanova.Dto;

import com.asss.www.ApotekarskaUstanova.Entity.Town;

public class TownDto {
    private int id;
    private String name;
    private int municipality_id;
    private MunicipalityDto municipality;
    private int postalCode;

    public TownDto(Town town) {
        if (town == null) {
            throw new IllegalArgumentException("Town ne može biti null.");
        }

        this.id = town.getId();
        this.name = town.getName();
        this.municipality_id = (town.getMunicipality() != null) ? town.getMunicipality().getId() : 0;
        this.municipality = (town.getMunicipality() != null) ? new MunicipalityDto(town.getMunicipality()) : null;
        this.postalCode = town.getPostalCode();
    }

    public TownDto() {
    }

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

    public int getMunicipality_id() {
        return municipality_id;
    }

    public void setMunicipality_id(int municipality_id) {
        this.municipality_id = municipality_id;
    }

    public MunicipalityDto getMunicipality() {
        return municipality;
    }

    public void setMunicipality(MunicipalityDto municipality) {
        this.municipality = municipality;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }
}
