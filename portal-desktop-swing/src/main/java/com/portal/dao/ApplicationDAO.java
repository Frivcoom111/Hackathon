package com.portal.dao;

import com.portal.model.Application;
import com.portal.model.enums.ApplicationStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO extends BaseDAO {

    public List<Application> findAll() {
        List<Application> list = new ArrayList<>();
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
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) {
            System.err.println("Erro ao listar candidaturas: " + e.getMessage());
        }
        return list;
    }

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
