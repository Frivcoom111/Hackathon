package com.portal.model;

public class Address {
    private String id;
    private String street;
    private String number;
    private String complement;
    private String district;
    private String city;
    private String state;
    private String zipCode;

    public Address() {}

    public Address(String id, String street, String number, String complement,
                   String district, String city, String state, String zipCode) {
        this.id         = id;
        this.street     = street;
        this.number     = number;
        this.complement = complement;
        this.district   = district;
        this.city       = city;
        this.state      = state;
        this.zipCode    = zipCode;
    }

    public String getId()          { return id; }
    public String getStreet()      { return street; }
    public String getNumber()      { return number; }
    public String getComplement()  { return complement; }
    public String getDistrict()    { return district; }
    public String getCity()        { return city; }
    public String getState()       { return state; }
    public String getZipCode()     { return zipCode; }

    public void setId(String id)                  { this.id = id; }
    public void setStreet(String street)          { this.street = street; }
    public void setNumber(String number)          { this.number = number; }
    public void setComplement(String complement)  { this.complement = complement; }
    public void setDistrict(String district)      { this.district = district; }
    public void setCity(String city)              { this.city = city; }
    public void setState(String state)            { this.state = state; }
    public void setZipCode(String zipCode)        { this.zipCode = zipCode; }

    public String formatarCep() {
        if (zipCode == null || zipCode.length() != 8) return zipCode;
        return zipCode.substring(0, 5) + "-" + zipCode.substring(5);
    }
}
