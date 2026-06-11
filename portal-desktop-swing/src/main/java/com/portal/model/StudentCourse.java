package com.portal.model;

import com.portal.model.enums.StudentCourseStatus;
import java.time.LocalDateTime;

public class StudentCourse {

    private String id;
    private String studentId;
    private String courseId;
    private StudentCourseStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public StudentCourse() {}

    public StudentCourse(String id, String studentId, String courseId,
                         StudentCourseStatus status, LocalDateTime startedAt,
                         LocalDateTime finishedAt, LocalDateTime createdAt,
                         LocalDateTime updatedAt) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.status = status;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId()                    { return id; }
    public String getStudentId()             { return studentId; }
    public String getCourseId()              { return courseId; }
    public StudentCourseStatus getStatus()   { return status; }
    public LocalDateTime getStartedAt()      { return startedAt; }
    public LocalDateTime getFinishedAt()     { return finishedAt; }
    public LocalDateTime getCreatedAt()      { return createdAt; }
    public LocalDateTime getUpdatedAt()      { return updatedAt; }

    public void setId(String id)                          { this.id = id; }
    public void setStudentId(String studentId)            { this.studentId = studentId; }
    public void setCourseId(String courseId)              { this.courseId = courseId; }
    public void setStatus(StudentCourseStatus status)     { this.status = status; }
    public void setStartedAt(LocalDateTime startedAt)     { this.startedAt = startedAt; }
    public void setFinishedAt(LocalDateTime finishedAt)   { this.finishedAt = finishedAt; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)     { this.updatedAt = updatedAt; }
}
