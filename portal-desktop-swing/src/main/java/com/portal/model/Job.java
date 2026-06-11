package com.portal.model;

import com.portal.model.enums.JobModality;
import com.portal.model.enums.JobStatus;
import java.time.LocalDateTime;

public class Job {

    private String id;
    private String companyId;
    private String courseId;
    private String title;
    private String description;
    private String area;
    private String requirements;
    private Double salary;
    private String location;
    private JobModality modality;
    private JobStatus status;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Job() {}

    public Job(String id, String companyId, String courseId, String title,
               String description, String area, String requirements, Double salary,
               String location, JobModality modality, JobStatus status,
               LocalDateTime deletedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.companyId = companyId;
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.area = area;
        this.requirements = requirements;
        this.salary = salary;
        this.location = location;
        this.modality = modality;
        this.status = status;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId()               { return id; }
    public String getCompanyId()        { return companyId; }
    public String getCourseId()         { return courseId; }
    public String getTitle()            { return title; }
    public String getDescription()      { return description; }
    public String getArea()             { return area; }
    public String getRequirements()     { return requirements; }
    public Double getSalary()           { return salary; }
    public String getLocation()         { return location; }
    public JobModality getModality()    { return modality; }
    public JobStatus getStatus()        { return status; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(String id)                      { this.id = id; }
    public void setCompanyId(String companyId)        { this.companyId = companyId; }
    public void setCourseId(String courseId)          { this.courseId = courseId; }
    public void setTitle(String title)                { this.title = title; }
    public void setDescription(String description)    { this.description = description; }
    public void setArea(String area)                  { this.area = area; }
    public void setRequirements(String requirements)  { this.requirements = requirements; }
    public void setSalary(Double salary)              { this.salary = salary; }
    public void setLocation(String location)          { this.location = location; }
    public void setModality(JobModality modality)     { this.modality = modality; }
    public void setStatus(JobStatus status)           { this.status = status; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
