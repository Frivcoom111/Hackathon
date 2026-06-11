package com.portal.model;

import java.time.LocalDateTime;

public class Course {

    private String id;
    private String name;
    private String code;
    private int periods;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Course() {}

    public Course(String id, String name, String code, int periods, boolean active,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.periods = periods;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId()               { return id; }
    public String getName()             { return name; }
    public String getCode()             { return code; }
    public int getPeriods()             { return periods; }
    public boolean isActive()           { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(String id)                      { this.id = id; }
    public void setName(String name)                  { this.name = name; }
    public void setCode(String code)                  { this.code = code; }
    public void setPeriods(int periods)               { this.periods = periods; }
    public void setActive(boolean active)             { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
