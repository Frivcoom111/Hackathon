package com.portal.service;

import com.portal.dao.ApplicationDAO;
import com.portal.model.Application;
import com.portal.model.enums.ApplicationStatus;

import java.util.List;

public class ApplicationService {

    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    public List<Application> findAll() {
        return null;
    }

    public Application findById(String id) {
        return null;
    }

    public List<Application> findByStatus(ApplicationStatus status) {
        return null;
    }

    public List<Application> findByStudentId(String studentId) {
        return null;
    }

    public List<Application> findByJobId(String jobId) {
        return null;
    }
}
