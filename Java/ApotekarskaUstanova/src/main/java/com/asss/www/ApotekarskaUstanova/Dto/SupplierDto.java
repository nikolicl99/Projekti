package com.asss.www.ApotekarskaUstanova.Dto;

import com.asss.www.ApotekarskaUstanova.Entity.Supplier;

public class SupplierDto {
    private int id;
    private String name;
    private String email;
    private String phone;
    private int addressId; // ID adrese
    private AddressDto address; // Referenca na AddressDto

    // Konstruktori
    public SupplierDto() {
    }

    public SupplierDto(int id, String name, String email, String phone, int addressId, AddressDto address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.addressId = addressId;
        this.address = address;
    }

    public SupplierDto(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier ne može biti null.");
        }

        this.id = supplier.getId();
        this.name = supplier.getName();
        this.email = supplier.getEmail();
        this.phone = supplier.getPhone();
        this.addressId = (supplier.getAddress() != null) ? supplier.getAddress().getId() : 0;
        this.address = (supplier.getAddress() != null) ? new AddressDto(supplier.getAddress()) : null;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }
}