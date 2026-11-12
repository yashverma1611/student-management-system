package com.sms.model;

public class Student {
    private int id;
    private String rollNo;
    private String password;
    private String name;
    private String department;
    private String course;
    private int semester;
    private String email;
    private String contact;
    private String address;
    private String feeStatus; // PAID, DUE
    private double feePaid;
    private double feeDue;

    public Student() {}

    public Student(String rollNo, String password, String name, String department, String course, int semester, String email, String contact, String feeStatus, double feePaid, double feeDue) {
        this.rollNo = rollNo;
        this.password = password;
        this.name = name;
        this.department = department;
        this.course = course;
        this.semester = semester;
        this.email = email;
        this.contact = contact;
        this.feeStatus = feeStatus;
        this.feePaid = feePaid;
        this.feeDue = feeDue;
        this.address = "Lucknow";
    }

    public Student(int id, String rollNo, String password, String name, String department, String course, int semester, String email, String contact, String address, String feeStatus, double feePaid, double feeDue) {
        this.id = id;
        this.rollNo = rollNo;
        this.password = password;
        this.name = name;
        this.department = department;
        this.course = course;
        this.semester = semester;
        this.email = email;
        this.contact = contact;
        this.address = address;
        this.feeStatus = feeStatus;
        this.feePaid = feePaid;
        this.feeDue = feeDue;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getFeeStatus() { return feeStatus; }
    public void setFeeStatus(String feeStatus) { this.feeStatus = feeStatus; }

    public double getFeePaid() { return feePaid; }
    public void setFeePaid(double feePaid) { this.feePaid = feePaid; }

    public double getFeeDue() { return feeDue; }
    public void setFeeDue(double feeDue) { this.feeDue = feeDue; }
}
