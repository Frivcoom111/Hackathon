package com.portal.dao;

import com.portal.model.Company;
import com.portal.model.enums.CompanyStatus;

import java.util.List;

public class CompanyDAO extends BaseDAO {

    public List<Company> findAll() {
        return null;
    }

    public Company findById(String id) {
        return null;
    }

    public List<Company> findByStatus(CompanyStatus status) {
        return null;
    }

    public void save(Company company) {
    }

    public void update(Company company) {
    }

    public void updateStatus(String id, CompanyStatus status) {
    }
}
