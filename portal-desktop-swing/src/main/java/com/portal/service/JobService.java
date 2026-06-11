package com.portal.service;

import com.portal.dao.JobDAO;
import com.portal.model.Job;
import com.portal.model.enums.JobStatus;

import java.util.List;

public class JobService {

    private final JobDAO jobDAO = new JobDAO();

    public List<Job> findAll() {
        return null;
    }

    public Job findById(String id) {
        return null;
    }

    public List<Job> findByStatus(JobStatus status) {
        return null;
    }

    public List<Job> findByCompanyId(String companyId) {
        return null;
    }

    public List<Job> findByCourseId(String courseId) {
        return null;
    }
}
