package com.portal.dao;

import com.portal.model.Application;
import com.portal.model.enums.ApplicationStatus;

import java.sql.*; // Importa de uma vez Connection, PreparedStatement, ResultSet, SQLException...
import java.util.ArrayList;
import java.util.List;

/**
 * ApplicationDAO: DAO responsável por acessar os dados das CANDIDATURAS no banco.
 *
 * Aqui ficam as consultas relacionadas à tabela Application.
 */
public class ApplicationDAO extends BaseDAO {

    /**
     * Lista TODAS as candidaturas ativas (não excluídas), já trazendo o nome do aluno
     * e o título da vaga por meio de JOINs com as tabelas Student e Job.
     *
     * @return lista de candidaturas, ordenada da mais recente para a mais antiga.
     */
    public List<Application> findAll() {
        List<Application> list = new ArrayList<>();
        // JOIN cruza as tabelas: pega a candidatura e, junto, o nome do aluno e o título da vaga.
        // WHERE a.deletedAt IS NULL ignora candidaturas "excluídas logicamente".
        // ORDER BY a.createdAt DESC coloca as mais novas primeiro.
        String sql = """
                SELECT a.id, a.studentId, a.jobId, a.status,
                       s.name AS studentName, j.title AS jobTitle
                FROM Application a
                JOIN Student s ON s.id = a.studentId
                JOIN Job     j ON j.id = a.jobId
                WHERE a.deletedAt IS NULL
                ORDER BY a.createdAt DESC
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // Percorre cada linha do resultado e a transforma em um objeto Application.
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) {
            System.err.println("Erro ao listar candidaturas: " + e.getMessage());
        }
        return list;
    }

    /**
     * Converte UMA linha do ResultSet em um objeto Application (mapeamento).
     *
     * ApplicationStatus.valueOf(...) transforma o texto do banco (ex.: "APPROVED")
     * no valor correspondente do enum.
     */
    private Application map(ResultSet rs) throws SQLException {
        return new Application(
            rs.getString("id"),
            rs.getString("studentId"),
            rs.getString("jobId"),
            ApplicationStatus.valueOf(rs.getString("status")),
            rs.getString("studentName"),
            rs.getString("jobTitle")
        );
    }
}
