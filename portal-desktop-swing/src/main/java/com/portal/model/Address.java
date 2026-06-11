package com.portal.model;

import java.time.LocalDateTime;

public class Address {

    private String id;
    private String street;
    private String number;
    private String complement;
    private String district;
    private String city;
    private String state;
    private String zipCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Address() {}

    public Address(String id, String street, String number, String complement,
                   String district, String city, String state, String zipCode,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.district = district;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId()              { return id; }
    public String getStreet()          { return street; }
    public String getNumber()          { return number; }
    public String getComplement()      { return complement; }
    public String getDistrict()        { return district; }
    public String getCity()            { return city; }
    public String getState()           { return state; }
    public String getZipCode()         { return zipCode; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public LocalDateTime getUpdatedAt(){ return updatedAt; }

    public void setId(String id)                      { this.id = id; }
    public void setStreet(String street)              { this.street = street; }
    public void setNumber(String number)              { this.number = number; }
    public void setComplement(String complement)      { this.complement = complement; }
    public void setDistrict(String district)          { this.district = district; }
    public void setCity(String city)                  { this.city = city; }
    public void setState(String state)                { this.state = state; }
    public void setZipCode(String zipCode)            { this.zipCode = zipCode; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
