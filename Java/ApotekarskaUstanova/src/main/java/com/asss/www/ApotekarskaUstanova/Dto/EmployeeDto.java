package com.asss.www.ApotekarskaUstanova.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmployeeDto {

    @NotBlank(message = "Ime je obavezno")
    private String name;

    @NotBlank(message = "Prezime je obavezno")
    private String surname;

    @Email(message = "Unesite ispravan email")
    @NotBlank(message = "Email je obavezan")
    private String email;

    @NotBlank(message = "Šifra je obavezna")
    private String password;

    @NotBlank(message = "Telefon je obavezan")
    private String mobile;

    private String type; // Ovo je String, jer ComboBox vraća string vrednosti

    private int address;

    private static String profileImage;

    private String scaledProfileImage;

    // Getteri i setteri

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public static String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getScaledProfileImage() {
        return scaledProfileImage;
    }

    public void setScaledProfileImage(String scaledProfileImage) {
        this.scaledProfileImage = scaledProfileImage;
    }
}
