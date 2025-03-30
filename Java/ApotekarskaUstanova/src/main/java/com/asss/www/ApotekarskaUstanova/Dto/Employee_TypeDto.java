package com.asss.www.ApotekarskaUstanova.Dto;

public class Employee_TypeDto {
    private int id;
    private String name;


    public Employee_TypeDto() {
    }

    // Konstruktor sa svim poljima
    public Employee_TypeDto(int id, String name) {
        this.id = id;
        this.name = name;
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
}