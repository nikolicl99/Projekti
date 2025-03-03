package com.asss.www.ApotekarskaUstanova.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "type_of_employee")
public class Employee_Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "Type_Name") // Kolona u bazi podataka
    private String name; // Polje u entitetu (početno malo slovo)

    @Column(name = "can_add_employee")
    private boolean canAddEmployee;

    @Column(name = "can_edit_employee")
    private boolean canEditEmployee;

    @Column(name = "can_manage_inventory")
    private boolean canManageInventory;

    @Column(name = "can_view_reports")
    private boolean canViewReports;

    @Column(name = "can_generate_reports")
    private boolean canGenerateReports;

    @Column(name = "can_manage_roles")
    private boolean canManageRoles;

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

    public boolean isCanAddEmployee() {
        return canAddEmployee;
    }

    public void setCanAddEmployee(boolean canAddEmployee) {
        this.canAddEmployee = canAddEmployee;
    }

    public boolean isCanEditEmployee() {
        return canEditEmployee;
    }

    public void setCanEditEmployee(boolean canEditEmployee) {
        this.canEditEmployee = canEditEmployee;
    }

    public boolean isCanManageInventory() {
        return canManageInventory;
    }

    public void setCanManageInventory(boolean canManageInventory) {
        this.canManageInventory = canManageInventory;
    }

    public boolean isCanViewReports() {
        return canViewReports;
    }

    public void setCanViewReports(boolean canViewReports) {
        this.canViewReports = canViewReports;
    }

    public boolean isCanGenerateReports() {
        return canGenerateReports;
    }

    public void setCanGenerateReports(boolean canGenerateReports) {
        this.canGenerateReports = canGenerateReports;
    }

    public boolean isCanManageRoles() {
        return canManageRoles;
    }

    public void setCanManageRoles(boolean canManageRoles) {
        this.canManageRoles = canManageRoles;
    }
}
