package com.portal.model;

import com.portal.model.enums.JobModality;
import com.portal.model.enums.JobStatus;

import java.math.BigDecimal;

public class Job {
    private String id;
    private String title;
    private String area;
    private String location;
    private JobModality modality;
    private JobStatus status;
    private BigDecimal salary;

    public Job() {}

    public Job(String id, String title, String area, String location,
               JobModality modality, JobStatus status, BigDecimal salary) {
        this.id = id;
        this.title = title;
        this.area = area;
        this.location = location;
        this.modality = modality;
        this.status = status;
        this.salary = salary;
    }

    public String getId()           { return id; }
    public String getTitle()        { return title; }
    public String getArea()         { return area; }
    public String getLocation()     { return location; }
    public JobModality getModality(){ return modality; }
    public JobStatus getStatus()    { return status; }
    public BigDecimal getSalary()   { return salary; }

    public void setId(String id)                { this.id = id; }
    public void setTitle(String title)          { this.title = title; }
    public void setArea(String area)            { this.area = area; }
    public void setLocation(String location)    { this.location = location; }
    public void setModality(JobModality m)      { this.modality = m; }
    public void setStatus(JobStatus s)          { this.status = s; }
    public void setSalary(BigDecimal salary)    { this.salary = salary; }
}
