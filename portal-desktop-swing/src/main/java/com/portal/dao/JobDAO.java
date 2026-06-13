package com.portal.dao;

import com.portal.model.Job;
import com.portal.model.enums.JobModality;
import com.portal.model.enums.JobStatus;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAO extends BaseDAO {

    public List<Job> findAll() {
        List<Job> list = new ArrayList<>();
        String sql = """
                SELECT id, title, area, location, modality, status, salary
                FROM Job
                WHERE deletedAt IS NULL
                ORDER BY title
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) {
            System.err.println("Erro ao listar vagas: " + e.getMessage());
        }
        return list;
    }

    private Job map(ResultSet rs) throws SQLException {
        BigDecimal salary = rs.getBigDecimal("salary");
        return new Job(
            rs.getString("id"),
            rs.getString("title"),
            rs.getString("area"),
            rs.getString("location"),
            JobModality.valueOf(rs.getString("modality")),
            JobStatus.valueOf(rs.getString("status")),
            salary
        );
    }
}
