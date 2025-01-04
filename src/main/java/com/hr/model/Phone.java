package com.hr.model;

import jakarta.persistence.*;

@Entity
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public Phone() {}

    public Phone(Long id, String phoneNumber, Employee employee) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}