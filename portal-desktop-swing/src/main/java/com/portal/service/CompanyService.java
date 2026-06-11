package com.portal.service;

import com.portal.dao.CompanyDAO;
import com.portal.model.Company;
import com.portal.model.enums.CompanyStatus;

import java.util.List;

public class CompanyService {

    private final CompanyDAO companyDAO = new CompanyDAO();

    public List<Company> findAll() {
        return null;
    }

    public List<Company> findByStatus(CompanyStatus status) {
        return null;
    }

    public Company findById(String id) {
        return null;
    }

    public void analyse(String id) {
    }

    public void approve(String id) {
    }

    public void block(String id) {
    }
}
