package com.portal.model;

import com.portal.model.enums.Role;

public class User {
    private String id;
    private String email;
    private String password;
    private Role role;
    private boolean active;
    private Address address;

    public User() {}

    public User(String id, String email, String password, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getId()       { return id; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public Role getRole()       { return role; }
    public boolean isActive()   { return active; }
    public Address getAddress() { return address; }

    public void setId(String id)          { this.id = id; }
    public void setEmail(String email)    { this.email = email; }
    public void setPassword(String pw)    { this.password = pw; }
    public void setRole(Role role)        { this.role = role; }
    public void setActive(boolean active) { this.active = active; }
    public void setAddress(Address a)     { this.address = a; }
}
