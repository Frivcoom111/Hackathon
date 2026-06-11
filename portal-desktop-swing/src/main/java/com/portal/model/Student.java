package com.portal.model;

import java.time.LocalDateTime;

public class Student {

    private String id;
    private String userId;
    private String addressId;
    private String name;
    private String ra;
    private String cpf;
    private String phone;
    private boolean eligible;
    private String resumePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Student() {}

    public Student(String id, String userId, String addressId, String name,
                   String ra, String cpf, String phone, boolean eligible,
                   String resumePath, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.addressId = addressId;
        this.name = name;
        this.ra = ra;
        this.cpf = cpf;
        this.phone = phone;
        this.eligible = eligible;
        this.resumePath = resumePath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId()               { return id; }
    public String getUserId()           { return userId; }
    public String getAddressId()        { return addressId; }
    public String getName()             { return name; }
    public String getRa()               { return ra; }
    public String getCpf()              { return cpf; }
    public String getPhone()            { return phone; }
    public boolean isEligible()         { return eligible; }
    public String getResumePath()       { return resumePath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(String id)                      { this.id = id; }
    public void setUserId(String userId)              { this.userId = userId; }
    public void setAddressId(String addressId)        { this.addressId = addressId; }
    public void setName(String name)                  { this.name = name; }
    public void setRa(String ra)                      { this.ra = ra; }
    public void setCpf(String cpf)                    { this.cpf = cpf; }
    public void setPhone(String phone)                { this.phone = phone; }
    public void setEligible(boolean eligible)         { this.eligible = eligible; }
    public void setResumePath(String resumePath)      { this.resumePath = resumePath; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
