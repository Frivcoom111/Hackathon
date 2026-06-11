package com.portal.model;

import com.portal.model.enums.ApplicationStatus;
import java.time.LocalDateTime;

public class Application {

    private String id;
    private String studentId;
    private String jobId;
    private ApplicationStatus status;
    private String resumePath;
    private String coverLetter;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Application() {}

    public Application(String id, String studentId, String jobId,
                       ApplicationStatus status, String resumePath, String coverLetter,
                       LocalDateTime deletedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.jobId = jobId;
        this.status = status;
        this.resumePath = resumePath;
        this.coverLetter = coverLetter;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId()                  { return id; }
    public String getStudentId()           { return studentId; }
    public String getJobId()               { return jobId; }
    public ApplicationStatus getStatus()   { return status; }
    public String getResumePath()          { return resumePath; }
    public String getCoverLetter()         { return coverLetter; }
    public LocalDateTime getDeletedAt()    { return deletedAt; }
    public LocalDateTime getCreatedAt()    { return createdAt; }
    public LocalDateTime getUpdatedAt()    { return updatedAt; }

    public void setId(String id)                        { this.id = id; }
    public void setStudentId(String studentId)          { this.studentId = studentId; }
    public void setJobId(String jobId)                  { this.jobId = jobId; }
    public void setStatus(ApplicationStatus status)     { this.status = status; }
    public void setResumePath(String resumePath)        { this.resumePath = resumePath; }
    public void setCoverLetter(String coverLetter)      { this.coverLetter = coverLetter; }
    public void setDeletedAt(LocalDateTime deletedAt)   { this.deletedAt = deletedAt; }
    public void setCreatedAt(LocalDateTime createdAt)   { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)   { this.updatedAt = updatedAt; }
}
