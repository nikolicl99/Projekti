package com.example.biblioteka;

public class MyData {
    private final int id;
    private final String naslov;
    private final String autor;
    private final String zanr;
    private final int godinaIzdavanja;
    private int naruceno;

    public MyData(int id, String naslov, String autor, String zanr, int godinaIzdavanja, int naruceno) {
        this.id = id;
        this.naslov = naslov;
        this.autor = autor;
        this.zanr = zanr;
        this.godinaIzdavanja = godinaIzdavanja;
        this.naruceno = naruceno;
    }



    public int getId() {
        return id;
    }

    public String getNaslov() {
        return naslov;
    }

    public String getAutor() {
        return autor;
    }

    public String getZanr() {
        return zanr;
    }

    public int getGodinaIzdavanja() {
        return godinaIzdavanja;
    }

    public int getNaruceno() {
        return naruceno;
    }

    public void setNaruceno(int naruceno) {
        this.naruceno = naruceno;
    }

    @Override
    public String toString() {
        return naslov;
    }
}
