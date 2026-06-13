package com.portal.model;

import com.portal.model.enums.ApplicationStatus;

public class Application {
    private String id;
    private String studentId;
    private String jobId;
    private ApplicationStatus status;
    private String studentName;
    private String jobTitle;

    public Application() {}

    public Application(String id, String studentId, String jobId, ApplicationStatus status,
                       String studentName, String jobTitle) {
        this.id = id;
        this.studentId = studentId;
        this.jobId = jobId;
        this.status = status;
        this.studentName = studentName;
        this.jobTitle = jobTitle;
    }

    public String getId()                  { return id; }
    public String getStudentId()           { return studentId; }
    public String getJobId()               { return jobId; }
    public ApplicationStatus getStatus()   { return status; }
    public String getStudentName()         { return studentName; }
    public String getJobTitle()            { return jobTitle; }

    public void setId(String id)                    { this.id = id; }
    public void setStudentId(String studentId)      { this.studentId = studentId; }
    public void setJobId(String jobId)              { this.jobId = jobId; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public void setStudentName(String studentName)  { this.studentName = studentName; }
    public void setJobTitle(String jobTitle)        { this.jobTitle = jobTitle; }
}
