package com.portal.model;

import com.portal.model.enums.CompanyStatus;

public class Company {
    private String id;
    private String name;
    private String cnpj;
    private String phone;
    private CompanyStatus status;

    public Company() {}

    public Company(String id, String name, String cnpj, String phone, CompanyStatus status) {
        this.id = id;
        this.name = name;
        this.cnpj = cnpj;
        this.phone = phone;
        this.status = status;
    }

    public String getId()            { return id; }
    public String getName()          { return name; }
    public String getCnpj()          { return cnpj; }
    public String getPhone()         { return phone; }
    public CompanyStatus getStatus() { return status; }

    public void setId(String id)                  { this.id = id; }
    public void setName(String name)              { this.name = name; }
    public void setCnpj(String cnpj)              { this.cnpj = cnpj; }
    public void setPhone(String phone)            { this.phone = phone; }
    public void setStatus(CompanyStatus status)   { this.status = status; }
}
