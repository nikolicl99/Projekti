package com.asss.www.ApotekarskaUstanova.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "status_name", nullable = false, unique = true)
    private String name;

    // Konstruktori

    public Status() {
    }

    public Status(String name) {
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
}
