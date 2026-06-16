package com.portal.dao;

import com.portal.model.DashboardStats; // Objeto que agrupa os números do painel.

import java.sql.Connection;
import java.sql.PreparedStatement; // Executa SQL de forma segura (contra SQL Injection).
import java.sql.ResultSet;

/**
 * DashboardDAO: DAO responsável por buscar os números-resumo da tela inicial (dashboard).
 *
 * Em vez de fazer quatro consultas separadas, ele usa UMA única consulta com
 * subconsultas (COUNT) para trazer todos os totais de uma só vez — mais eficiente.
 */
public class DashboardDAO extends BaseDAO {

    /**
     * Busca no banco as quatro estatísticas exibidas no painel.
     *
     * @return um DashboardStats com os totais; em caso de erro, devolve tudo zerado.
     */
    public DashboardStats getStats() {
        // SQL com quatro subconsultas, cada uma contando um tipo de registro:
        // - empresas pendentes de aprovação;
        // - vagas ativas (e não excluídas — deletedAt IS NULL é "exclusão lógica");
        // - candidaturas em aberto (pendentes ou em análise);
        // - alunos aptos (isEligible = 1).
        // O bloco """...""" é um "text block": uma string de várias linhas.
        String sql = """
                SELECT
                  (SELECT COUNT(*) FROM Company WHERE status = 'PENDING') AS empresasPendentes,
                  (SELECT COUNT(*) FROM Job WHERE status = 'ACTIVE' AND deletedAt IS NULL) AS vagasAtivas,
                  (SELECT COUNT(*) FROM Application WHERE status IN ('PENDING','ANALYSING') AND deletedAt IS NULL) AS candidaturasAbertas,
                  (SELECT COUNT(*) FROM Student WHERE isEligible = 1) AS alunosAptos
                """;
        // try-with-resources: conexão, statement e resultado são fechados automaticamente.
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // A consulta retorna uma única linha com os quatro totais.
            if (rs.next()) {
                return new DashboardStats(
                    rs.getInt("empresasPendentes"),
                    rs.getInt("vagasAtivas"),
                    rs.getInt("candidaturasAbertas"),
                    rs.getInt("alunosAptos")
                );
            }
        } catch (Exception e) {
            // Em caso de falha, registra no console (mas não quebra a aplicação).
            System.err.println("Erro ao buscar estatísticas do dashboard: " + e.getMessage());
        }
        // Valor padrão seguro: se algo der errado, o painel mostra zeros em vez de travar.
        return new DashboardStats(0, 0, 0, 0);
    }
}
