package com.portal.service;

import com.portal.dao.CompanyDAO;
import com.portal.dao.UserDAO;
import com.portal.model.Company;
import com.portal.model.enums.CompanyStatus;

import java.util.List;

public class CompanyService {

    private final CompanyDAO dao     = new CompanyDAO();
    private final UserDAO    userDAO = new UserDAO();

    public List<Company> listar() {
        return dao.findAll();
    }

    public List<Company> listarPorStatus(CompanyStatus status) {
        return dao.findByStatus(status);
    }

    public void analisar(Company company) throws ServiceException {
        if (company.getStatus() != CompanyStatus.PENDING) {
            throw new ServiceException("Apenas empresas PENDENTES podem ser movidas para análise.");
        }
        dao.updateStatus(company.getId(), CompanyStatus.ANALYSING);
        // Empresa não está APPROVED — usuários vinculados devem ficar inativos
        userDAO.setActiveByCompany(company.getId(), false);
        company.setStatus(CompanyStatus.ANALYSING);
    }

    public void aprovar(Company company) throws ServiceException {
        if (company.getStatus() == CompanyStatus.APPROVED) {
            throw new ServiceException("Empresa já está aprovada.");
        }
        dao.updateStatus(company.getId(), CompanyStatus.APPROVED);
        // Empresa aprovada — reativa todos os usuários vinculados
        userDAO.setActiveByCompany(company.getId(), true);
        company.setStatus(CompanyStatus.APPROVED);
    }

    public void bloquear(Company company) throws ServiceException {
        if (company.getStatus() == CompanyStatus.BLOCKED) {
            throw new ServiceException("Empresa já está bloqueada.");
        }
        dao.updateStatus(company.getId(), CompanyStatus.BLOCKED);
        // Empresa bloqueada — desativa todos os usuários vinculados
        userDAO.setActiveByCompany(company.getId(), false);
        company.setStatus(CompanyStatus.BLOCKED);
    }
}
