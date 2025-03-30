package com.asss.www.ApotekarskaUstanova.Dto;

import com.asss.www.ApotekarskaUstanova.Entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDto {
    private int id;
    private String name;
    private int dosage;
    private int subcategory_id;
    private SubcategoryDto subcategoryDto;
    private String description;
    private Double purchasePrice;
    private Double sellingPrice;
    private Integer stockQuantity;
    private int minQuantity;

    public ProductDto(int id, String name, int dosage, int subcategory_id, String description, Double purchasePrice, Double sellingPrice, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.subcategory_id = subcategory_id;
        this.description = description;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
    }

    // Primary constructor with all fields
    public ProductDto(int id, String name, int dosage, SubcategoryDto subcategoryDto, String description, Double purchasePrice, Double sellingPrice, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.subcategoryDto = subcategoryDto;
        this.description = description;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
    }

    // Constructor with minimal fields (id, name, sellingPrice)
    public ProductDto(int id, String name, Double sellingPrice) {
        this(id, name, 0, null, null, null, sellingPrice, null); // Call primary constructor
    }

    // Default constructor
    public ProductDto() {
    }

    public ProductDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.dosage = product.getDosage();
        this.subcategory_id = product.getSubcategory().getId();
        this.description = product.getDescription();
        this.purchasePrice = product.getPurchasePrice();
        this.sellingPrice = product.getSellingPrice();
        this.stockQuantity = product.getStockQuantity();

        // If the Product entity has a Subcategory object, you can map it to SubcategoryDto
        if (product.getSubcategory() != null) {
            this.subcategoryDto = new SubcategoryDto(product.getSubcategory());
        }
    }

    // Getters and setters
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

    public int getDosage() {
        return dosage;
    }

    public void setDosage(int dosage) {
        this.dosage = dosage;
    }

    public int getSubcategory_id() {
        return subcategory_id;
    }

    public void setSubcategory_id(int subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public SubcategoryDto getSubcategoryDto() {
        return subcategoryDto;
    }

    public void setSubcategoryDto(SubcategoryDto subcategoryDto) {
        this.subcategoryDto = subcategoryDto;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }
}