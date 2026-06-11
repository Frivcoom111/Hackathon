package com.portal.model;

import com.portal.model.enums.ApplicationStatus;

public class Application {
    private String id;
    private String studentId;
    private String jobId;
    private ApplicationStatus status;

    public Application() {}

    public Application(String id, String studentId, String jobId, ApplicationStatus status) {
        this.id = id;
        this.studentId = studentId;
        this.jobId = jobId;
        this.status = status;
    }

    public String getId()                  { return id; }
    public String getStudentId()           { return studentId; }
    public String getJobId()               { return jobId; }
    public ApplicationStatus getStatus()   { return status; }

    public void setId(String id)                    { this.id = id; }
    public void setStudentId(String studentId)      { this.studentId = studentId; }
    public void setJobId(String jobId)              { this.jobId = jobId; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
}
