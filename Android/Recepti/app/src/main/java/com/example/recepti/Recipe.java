package com.example.recepti;

import java.util.Date;

public class Recipe {
    private String id;
    private String name;
    private String imagePath;
    private boolean isBaked;
    private boolean isSweet;
    private String temperature;
    private String time;
    private String ingredients;
    private String instructions;
    private String firestoreId;
    private boolean isLocal;
    private long lastModified; // Vreme poslednje izmene u milisekundama
    private long createdAt;    // Vreme kreiranja

    // Konstruktori
    public Recipe() {
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
    }

    public Recipe(String name, String imagePath, boolean isBaked, boolean isSweet,
                  String temperature, String time, String ingredients, String instructions) {
        this();
        this.name = name;
        this.imagePath = imagePath;
        this.isBaked = isBaked;
        this.isSweet = isSweet;
        this.temperature = temperature;
        this.time = time;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    // Getteri i Setteri
    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
        this.lastModified = System.currentTimeMillis();
    }

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
        this.lastModified = System.currentTimeMillis();
    }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        this.lastModified = System.currentTimeMillis();
    }

    public boolean isBaked() { return isBaked; }
    public void setBaked(boolean baked) {
        isBaked = baked;
        this.lastModified = System.currentTimeMillis();
    }

    public boolean isSweet() { return isSweet; }
    public void setSweet(boolean sweet) {
        isSweet = sweet;
        this.lastModified = System.currentTimeMillis();
    }

    public String getTemperature() { return temperature; }
    public void setTemperature(String temperature) {
        this.temperature = temperature;
        this.lastModified = System.currentTimeMillis();
    }

    public String getTime() { return time; }
    public void setTime(String time) {
        this.time = time;
        this.lastModified = System.currentTimeMillis();
    }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
        this.lastModified = System.currentTimeMillis();
    }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
        this.lastModified = System.currentTimeMillis();
    }

    // Nove metode za timestamp
    public long getLastModified() { return lastModified; }
    public void setLastModified(long lastModified) { this.lastModified = lastModified; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // Pomocne metode za prikaz vremena
    public String getLastModifiedFormatted() {
        Date date = new Date(lastModified);
        return android.text.format.DateFormat.format("dd.MM.yyyy HH:mm", date).toString();
    }

    public String getCreatedAtFormatted() {
        Date date = new Date(createdAt);
        return android.text.format.DateFormat.format("dd.MM.yyyy HH:mm", date).toString();
    }

    // Dodatne metode
    public String getFirestoreId() { return firestoreId; }
    public void setFirestoreId(String firestoreId) { this.firestoreId = firestoreId; }

    public boolean isLocal() { return isLocal; }
    public void setLocal(boolean local) { isLocal = local; }

    @Override
    public String toString() {
        return name + " (" + (isSweet ? "slatko" : "slano") + ")";
    }
}