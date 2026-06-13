package com.portal.model;

import com.portal.model.enums.CompanyMemberRole;

public class CompanyMember {
    private String id;
    private String companyId;
    private String userId;
    private String name;
    private String cpf;
    private String phone;
    private CompanyMemberRole role;
    private String email;
    private boolean userActive;

    public CompanyMember() {}

    public CompanyMember(String id, String companyId, String userId,
                         String name, String cpf, String phone,
                         CompanyMemberRole role, String email, boolean userActive) {
        this.id = id;
        this.companyId = companyId;
        this.userId = userId;
        this.name = name;
        this.cpf = cpf;
        this.phone = phone;
        this.role = role;
        this.email = email;
        this.userActive = userActive;
    }

    public String getId()              { return id; }
    public String getCompanyId()       { return companyId; }
    public String getUserId()          { return userId; }
    public String getName()            { return name; }
    public String getCpf()             { return cpf; }
    public String getPhone()           { return phone; }
    public CompanyMemberRole getRole() { return role; }
    public String getEmail()           { return email; }
    public boolean isUserActive()      { return userActive; }

    public void setId(String id)                        { this.id = id; }
    public void setCompanyId(String companyId)          { this.companyId = companyId; }
    public void setUserId(String userId)                { this.userId = userId; }
    public void setName(String name)                    { this.name = name; }
    public void setCpf(String cpf)                      { this.cpf = cpf; }
    public void setPhone(String phone)                  { this.phone = phone; }
    public void setRole(CompanyMemberRole role)         { this.role = role; }
    public void setEmail(String email)                  { this.email = email; }
    public void setUserActive(boolean userActive)       { this.userActive = userActive; }
}
