package com.portal.model;

import com.portal.model.enums.Role;

public class User {

    private String id;
    private String email;
    private String password;
    private Role role;
    private boolean active;

    public User() {}

    public User(String id, String email, String password, Role role, boolean active) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public String getId()       { return id; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public Role   getRole()     { return role; }
    public boolean isActive()   { return active; }

    public void setId(String id)           { this.id = id; }
    public void setEmail(String email)     { this.email = email; }
    public void setPassword(String p)      { this.password = p; }
    public void setRole(Role role)         { this.role = role; }
    public void setActive(boolean active)  { this.active = active; }
}
