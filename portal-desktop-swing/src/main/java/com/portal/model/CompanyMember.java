package com.portal.model;

import com.portal.model.enums.CompanyMemberRole;
import java.time.LocalDateTime;

public class CompanyMember {

    private String id;
    private String companyId;
    private String userId;
    private String addressId;
    private CompanyMemberRole role;
    private String name;
    private String cpf;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CompanyMember() {}

    public CompanyMember(String id, String companyId, String userId, String addressId,
                         CompanyMemberRole role, String name, String cpf, String phone,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.companyId = companyId;
        this.userId = userId;
        this.addressId = addressId;
        this.role = role;
        this.name = name;
        this.cpf = cpf;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId()                  { return id; }
    public String getCompanyId()           { return companyId; }
    public String getUserId()              { return userId; }
    public String getAddressId()           { return addressId; }
    public CompanyMemberRole getRole()     { return role; }
    public String getName()                { return name; }
    public String getCpf()                 { return cpf; }
    public String getPhone()               { return phone; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public LocalDateTime getUpdatedAt()    { return updatedAt; }

    public void setId(String id)                        { this.id = id; }
    public void setCompanyId(String companyId)          { this.companyId = companyId; }
    public void setUserId(String userId)                { this.userId = userId; }
    public void setAddressId(String addressId)          { this.addressId = addressId; }
    public void setRole(CompanyMemberRole role)         { this.role = role; }
    public void setName(String name)                    { this.name = name; }
    public void setCpf(String cpf)                      { this.cpf = cpf; }
    public void setPhone(String phone)                  { this.phone = phone; }
    public void setCreatedAt(LocalDateTime createdAt)   { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)   { this.updatedAt = updatedAt; }
}
