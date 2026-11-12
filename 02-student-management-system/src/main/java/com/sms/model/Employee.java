package com.sms.model;

import java.sql.Timestamp;

public class Employee {
    private String empId;
    private String name;
    private String department;
    private String password;
    private String role; // STAFF, COUNSELOR, ACCOUNTANT
    private Timestamp createdAt;

    public Employee() {}

    public Employee(String empId, String name, String department, String password, String role) {
        this.empId = empId;
        this.name = name;
        this.department = department;
        this.password = password;
        this.role = role;
    }

    public Employee(String empId, String name, String department, String password, String role, Timestamp createdAt) {
        this.empId = empId;
        this.name = name;
        this.department = department;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
