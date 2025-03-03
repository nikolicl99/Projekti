package com.asss.www.ApotekarskaUstanova.Entity;


import jakarta.persistence.*;


@Entity
@Table(name = "municipality") // Naziv tabele u bazi podataka
public class Municipality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatsko generisanje ID-a
    @Column(name = "Id")
    private int id;

    @Column(name = "Name")
    private String name;

    // Default konstruktor
    public Municipality() {}

    // Parametrizovani konstruktor
    public Municipality(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return name;
    }
}
