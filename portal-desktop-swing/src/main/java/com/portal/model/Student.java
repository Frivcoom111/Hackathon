package com.portal.model;

public class Student {
    private String id;
    private String name;
    private String ra;
    private String cpf;
    private boolean eligible;

    public Student() {}

    public Student(String id, String name, String ra, String cpf, boolean eligible) {
        this.id = id;
        this.name = name;
        this.ra = ra;
        this.cpf = cpf;
        this.eligible = eligible;
    }

    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getRa()       { return ra; }
    public String getCpf()      { return cpf; }
    public boolean isEligible() { return eligible; }

    public void setId(String id)          { this.id = id; }
    public void setName(String name)      { this.name = name; }
    public void setRa(String ra)          { this.ra = ra; }
    public void setCpf(String cpf)        { this.cpf = cpf; }
    public void setEligible(boolean e)    { this.eligible = e; }
}
