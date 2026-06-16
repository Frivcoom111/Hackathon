package com.portal.service;

import com.portal.dao.ApplicationDAO;
import com.portal.dao.CompanyDAO;
import com.portal.dao.JobDAO;
import com.portal.dao.StudentDAO;
import com.portal.util.ReportExporter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ReportService: serviço que ORQUESTRA a geração dos relatórios.
 *
 * Para cada tipo de relatório, ele: (1) busca os dados no DAO correspondente e
 * (2) pede ao ReportExporter para escrever o arquivo. Também monta o nome do arquivo
 * com data/hora, para que cada relatório gerado tenha um nome único.
 */
public class ReportService {

    // Um DAO para cada tipo de dado que pode entrar em um relatório.
    private final CompanyDAO     companyDAO     = new CompanyDAO();
    private final StudentDAO     studentDAO     = new StudentDAO();
    private final JobDAO         jobDAO         = new JobDAO();
    private final ApplicationDAO applicationDAO = new ApplicationDAO();

    // Formato do "carimbo" de data/hora usado no nome do arquivo (ex.: 20260616_143005).
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    /**
     * Gera o relatório de empresas dentro da pasta informada.
     * @return o caminho completo do arquivo gerado.
     */
    public String gerarEmpresas(String pasta) throws IOException {
        String path = buildPath(pasta, "relatorio_empresas");
        ReportExporter.exportCompanies(companyDAO.findAll(), path);
        return path;
    }

    /** Gera o relatório de alunos. @return o caminho do arquivo gerado. */
    public String gerarAlunos(String pasta) throws IOException {
        String path = buildPath(pasta, "relatorio_alunos");
        ReportExporter.exportStudents(studentDAO.findAll(), path);
        return path;
    }

    /** Gera o relatório de vagas. @return o caminho do arquivo gerado. */
    public String gerarVagas(String pasta) throws IOException {
        String path = buildPath(pasta, "relatorio_vagas");
        ReportExporter.exportJobs(jobDAO.findAll(), path);
        return path;
    }

    /** Gera o relatório de candidaturas. @return o caminho do arquivo gerado. */
    public String gerarCandidaturas(String pasta) throws IOException {
        String path = buildPath(pasta, "relatorio_candidaturas");
        ReportExporter.exportApplications(applicationDAO.findAll(), path);
        return path;
    }

    /**
     * Monta o caminho completo do arquivo de relatório.
     * Junta: pasta + separador do sistema + prefixo + "_" + data/hora + ".txt".
     *
     * File.separator é "\" no Windows e "/" no Linux/Mac — assim o código funciona
     * em qualquer sistema operacional.
     */
    private String buildPath(String pasta, String prefixo) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP);
        return pasta + java.io.File.separator + prefixo + "_" + timestamp + ".txt";
    }
}
