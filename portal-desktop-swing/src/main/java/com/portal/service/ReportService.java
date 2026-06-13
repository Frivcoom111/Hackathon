package com.portal.service;

import com.portal.dao.ApplicationDAO;
import com.portal.dao.CompanyDAO;
import com.portal.dao.JobDAO;
import com.portal.dao.StudentDAO;
import com.portal.util.ReportExporter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportService {

    private final CompanyDAO     companyDAO     = new CompanyDAO();
    private final StudentDAO     studentDAO     = new StudentDAO();
    private final JobDAO         jobDAO         = new JobDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public String gerarEmpresas(String pasta) throws IOException {
        String path = buildPath(pasta, "relatorio_empresas");
        ReportExporter.exportCompanies(companyDAO.findAll(), path);
        return path;
    }

    public String gerarAlunos(String pasta) throws IOException {
        String path = buildPath(pasta, "relatorio_alunos");
        ReportExporter.exportStudents(studentDAO.findAll(), path);
        return path;
    }

    public String gerarVagas(String pasta) throws IOException {
        String path = buildPath(pasta, "relatorio_vagas");
        ReportExporter.exportJobs(jobDAO.findAll(), path);
        return path;
    }

    public String gerarCandidaturas(String pasta) throws IOException {
        String path = buildPath(pasta, "relatorio_candidaturas");
        ReportExporter.exportApplications(applicationDAO.findAll(), path);
        return path;
    }

    private String buildPath(String pasta, String prefixo) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP);
        return pasta + java.io.File.separator + prefixo + "_" + timestamp + ".txt";
    }
}
