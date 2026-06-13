package com.portal.dao;

import com.portal.model.Company;
import com.portal.model.enums.CompanyStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO extends BaseDAO {

    public List<Company> findAll() {
        List<Company> list = new ArrayList<>();
        String sql = "SELECT id, name, cnpj, description, phone, status FROM Company ORDER BY name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) {
            System.err.println("Erro ao listar empresas: " + e.getMessage());
        }
        return list;
    }

    public List<Company> findByStatus(CompanyStatus status) {
        List<Company> list = new ArrayList<>();
        String sql = "SELECT id, name, cnpj, description, phone, status FROM Company WHERE status = ? ORDER BY name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (Exception e) {
            System.err.println("Erro ao filtrar empresas: " + e.getMessage());
        }
        return list;
    }

    public Company findById(String id) {
        String sql = "SELECT id, name, cnpj, description, phone, status FROM Company WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar empresa: " + e.getMessage());
        }
        return null;
    }

    public void updateStatus(String id, CompanyStatus status) {
        String sql = "UPDATE Company SET status = ?, updatedAt = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Sao_Paulo"))));
            ps.setString(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar status da empresa: " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar status da empresa.", e);
        }
    }

    private Company map(ResultSet rs) throws SQLException {
        return new Company(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("cnpj"),
            rs.getString("description"),
            rs.getString("phone"),
            CompanyStatus.valueOf(rs.getString("status"))
        );
    }
}
