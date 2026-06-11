package com.portal.model;

import java.time.LocalDateTime;

public class Certificate {

    private String id;
    private String studentId;
    private String name;
    private String institution;
    private LocalDateTime issuedAt;
    private String filePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Certificate() {}

    public Certificate(String id, String studentId, String name, String institution,
                       LocalDateTime issuedAt, String filePath,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.institution = institution;
        this.issuedAt = issuedAt;
        this.filePath = filePath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId()               { return id; }
    public String getStudentId()        { return studentId; }
    public String getName()             { return name; }
    public String getInstitution()      { return institution; }
    public LocalDateTime getIssuedAt()  { return issuedAt; }
    public String getFilePath()         { return filePath; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(String id)                      { this.id = id; }
    public void setStudentId(String studentId)        { this.studentId = studentId; }
    public void setName(String name)                  { this.name = name; }
    public void setInstitution(String institution)    { this.institution = institution; }
    public void setIssuedAt(LocalDateTime issuedAt)   { this.issuedAt = issuedAt; }
    public void setFilePath(String filePath)          { this.filePath = filePath; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
