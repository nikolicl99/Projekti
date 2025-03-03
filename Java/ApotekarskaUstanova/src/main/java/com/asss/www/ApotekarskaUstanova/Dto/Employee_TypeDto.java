package com.asss.www.ApotekarskaUstanova.Dto;

public class Employee_TypeDto {
    private String name;
    private boolean canAddEmployee;
    private boolean canEditEmployee;
    private boolean canManageInventory;
    private boolean canViewReports;
    private boolean canGenerateReports;
    private boolean canManageRoles;

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