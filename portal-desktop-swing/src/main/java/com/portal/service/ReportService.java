package com.portal.service;

import com.portal.dao.ApplicationDAO;
import com.portal.dao.CompanyDAO;
import com.portal.dao.JobDAO;
import com.portal.dao.StudentDAO;
import com.portal.model.Application;
import com.portal.model.Company;
import com.portal.model.Job;
import com.portal.model.Student;

import java.util.List;

public class ReportService {

    private final CompanyDAO companyDAO         = new CompanyDAO();
    private final StudentDAO studentDAO         = new StudentDAO();
    private final JobDAO jobDAO                 = new JobDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    public List<Company> getCompaniesReport() {
        return null;
    }

    public List<Student> getStudentsReport() {
        return null;
    }

    public List<Job> getJobsReport() {
        return null;
    }

    public List<Application> getApplicationsReport() {
        return null;
    }
}
