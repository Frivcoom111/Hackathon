package com.portal.model;

import com.portal.model.enums.CompanyStatus;
import java.time.LocalDateTime;

public class Company {

    private String id;
    private String addressId;
    private String name;
    private String cnpj;
    private String description;
    private String phone;
    private CompanyStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Company() {}

    public Company(String id, String addressId, String name, String cnpj,
                   String description, String phone, CompanyStatus status,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.addressId = addressId;
        this.name = name;
        this.cnpj = cnpj;
        this.description = description;
        this.phone = phone;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId()               { return id; }
    public String getAddressId()        { return addressId; }
    public String getName()             { return name; }
    public String getCnpj()             { return cnpj; }
    public String getDescription()      { return description; }
    public String getPhone()            { return phone; }
    public CompanyStatus getStatus()    { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(String id)                      { this.id = id; }
    public void setAddressId(String addressId)        { this.addressId = addressId; }
    public void setName(String name)                  { this.name = name; }
    public void setCnpj(String cnpj)                  { this.cnpj = cnpj; }
    public void setDescription(String description)    { this.description = description; }
    public void setPhone(String phone)                { this.phone = phone; }
    public void setStatus(CompanyStatus status)       { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
