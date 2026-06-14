package com.portal.dao;

import com.portal.model.DashboardStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardDAO extends BaseDAO {

    public DashboardStats getStats() {
        String sql = """
                SELECT
                  (SELECT COUNT(*) FROM Company WHERE status = 'PENDING') AS empresasPendentes,
                  (SELECT COUNT(*) FROM Job WHERE status = 'ACTIVE' AND deletedAt IS NULL) AS vagasAtivas,
                  (SELECT COUNT(*) FROM Application WHERE status IN ('PENDING','ANALYSING') AND deletedAt IS NULL) AS candidaturasAbertas,
                  (SELECT COUNT(*) FROM Student WHERE isEligible = 1) AS alunosAptos
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new DashboardStats(
                    rs.getInt("empresasPendentes"),
                    rs.getInt("vagasAtivas"),
                    rs.getInt("candidaturasAbertas"),
                    rs.getInt("alunosAptos")
                );
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar estatísticas do dashboard: " + e.getMessage());
        }
        return new DashboardStats(0, 0, 0, 0);
    }
}
