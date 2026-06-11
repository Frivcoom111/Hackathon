package com.portal.model;

import java.time.LocalDateTime;

public class Notification {

    private String id;
    private String userId;
    private String title;
    private String message;
    private String type;
    private boolean read;
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(String id, String userId, String title, String message,
                        String type, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = read;
        this.createdAt = createdAt;
    }

    public String getId()               { return id; }
    public String getUserId()           { return userId; }
    public String getTitle()            { return title; }
    public String getMessage()          { return message; }
    public String getType()             { return type; }
    public boolean isRead()             { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(String id)                      { this.id = id; }
    public void setUserId(String userId)              { this.userId = userId; }
    public void setTitle(String title)                { this.title = title; }
    public void setMessage(String message)            { this.message = message; }
    public void setType(String type)                  { this.type = type; }
    public void setRead(boolean read)                 { this.read = read; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
