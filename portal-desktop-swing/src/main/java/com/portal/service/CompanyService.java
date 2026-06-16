package com.portal.service;

import com.portal.dao.CompanyDAO;
import com.portal.dao.UserDAO;
import com.portal.model.Company;
import com.portal.model.enums.CompanyStatus;

import java.util.List;

/**
 * CompanyService: serviço com as REGRAS DE NEGÓCIO das EMPRESAS.
 *
 * Cuida do fluxo de aprovação das empresas (analisar → aprovar/bloquear). Um detalhe
 * importante: o status da empresa controla o ACESSO dos seus membros. Por isso, ao
 * mudar o status, este serviço também (des)ativa os usuários vinculados àquela empresa.
 */
public class CompanyService {

    private final CompanyDAO dao     = new CompanyDAO(); // Acesso às empresas.
    private final UserDAO    userDAO = new UserDAO();    // Usado para (des)ativar membros.

    /** Lista todas as empresas. */
    public List<Company> listar() {
        return dao.findAll();
    }

    /** Lista as empresas de um determinado status (ex.: só as PENDING). */
    public List<Company> listarPorStatus(CompanyStatus status) {
        return dao.findByStatus(status);
    }

    /**
     * Move uma empresa PENDENTE para o status "EM ANÁLISE".
     *
     * Regra: só faz sentido analisar quem está PENDING. Como a empresa ainda não está
     * aprovada, os usuários dela permanecem INATIVOS (sem acesso).
     *
     * @throws ServiceException se a empresa não estiver no status PENDING.
     */
    public void analisar(Company company) throws ServiceException {
        if (company.getStatus() != CompanyStatus.PENDING) {
            throw new ServiceException("Apenas empresas PENDENTES podem ser movidas para análise.");
        }
        dao.updateStatus(company.getId(), CompanyStatus.ANALYSING);
        // Empresa não está APPROVED — usuários vinculados devem ficar inativos.
        userDAO.setActiveByCompany(company.getId(), false);
        company.setStatus(CompanyStatus.ANALYSING); // Atualiza também o objeto em memória.
    }

    /**
     * Aprova uma empresa, liberando o acesso dos seus membros.
     *
     * @throws ServiceException se a empresa já estiver aprovada (evita ação redundante).
     */
    public void aprovar(Company company) throws ServiceException {
        if (company.getStatus() == CompanyStatus.APPROVED) {
            throw new ServiceException("Empresa já está aprovada.");
        }
        dao.updateStatus(company.getId(), CompanyStatus.APPROVED);
        // Empresa aprovada — reativa todos os usuários vinculados (eles ganham acesso).
        userDAO.setActiveByCompany(company.getId(), true);
        company.setStatus(CompanyStatus.APPROVED);
    }

    /**
     * Bloqueia uma empresa, retirando o acesso dos seus membros.
     *
     * @throws ServiceException se a empresa já estiver bloqueada.
     */
    public void bloquear(Company company) throws ServiceException {
        if (company.getStatus() == CompanyStatus.BLOCKED) {
            throw new ServiceException("Empresa já está bloqueada.");
        }
        dao.updateStatus(company.getId(), CompanyStatus.BLOCKED);
        // Empresa bloqueada — desativa todos os usuários vinculados (perdem o acesso).
        userDAO.setActiveByCompany(company.getId(), false);
        company.setStatus(CompanyStatus.BLOCKED);
    }
}
