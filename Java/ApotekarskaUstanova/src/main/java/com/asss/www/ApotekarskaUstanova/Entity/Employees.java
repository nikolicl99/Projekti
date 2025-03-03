package com.asss.www.ApotekarskaUstanova.Entity;


import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "Name")
    private String name;

    @Column(name = "Surname")
    private String surname;

    @Column(name = "Email")
    private String email;

    @Column(name = "Password")
    private String password;

    @ManyToOne
    @JoinColumn(name = "Type", referencedColumnName = "id", nullable = true)
    private Employee_Type employeeType;

    @Column(name = "Mobile")
    private String mobile;

    @ManyToOne // Veza sa tabelom "address" - svaki supplier ima jednu adresu
    @JoinColumn(name = "address", referencedColumnName = "Id") // Veza sa Address tabelom
    private Address address;

    @Column(name = "profile_image", columnDefinition = "LONGTEXT")
    private String profileImage;

    @Column(name = "scaled_profile_image", columnDefinition = "LONGTEXT")
    private String scaledProfileImage;


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getMobile() {
        return mobile;
    }

//    public int getEmployeeType() {
//        return employeeType;
//    }

    public Employee_Type getEmployeeType() {
        return employeeType;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmployeeType(Employee_Type employeeType) {
        this.employeeType = employeeType;
    }

    //    public void setEmployeeType(int employeeType) {
//        this.employeeType = employeeType;
//    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getProfileImage() {
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
