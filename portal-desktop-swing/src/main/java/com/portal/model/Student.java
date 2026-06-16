package com.portal.model;

public class Student {
    private String id;
    private String userId;
    private String name;
    private String ra;
    private String cpf;
    private String email;
    private String phone;
    private boolean eligible;
    private Address address;

    public Student() {}

    public Student(String id, String userId, String name, String ra, String cpf,
                   String email, String phone, boolean eligible) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.ra = ra;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        this.eligible = eligible;
    }

    public String getId()       { return id; }
    public String getUserId()   { return userId; }
    public String getName()     { return name; }
    public String getRa()       { return ra; }
    public String getCpf()      { return cpf; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
    public boolean isEligible() { return eligible; }
    public Address getAddress() { return address; }

    public void setId(String id)          { this.id = id; }
    public void setUserId(String userId)  { this.userId = userId; }
    public void setName(String name)      { this.name = name; }
    public void setRa(String ra)          { this.ra = ra; }
    public void setCpf(String cpf)        { this.cpf = cpf; }
    public void setEmail(String email)    { this.email = email; }
    public void setPhone(String phone)    { this.phone = phone; }
    public void setEligible(boolean e)    { this.eligible = e; }
    public void setAddress(Address a)     { this.address = a; }
}
