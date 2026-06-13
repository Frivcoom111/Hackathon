package com.portal.dao;

import com.portal.model.User;
import com.portal.model.enums.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class UserDAO extends BaseDAO {

    public void save(User user, Connection conn) throws Exception {
        String sql = "INSERT INTO User (id, email, password, role, isActive, createdAt, updatedAt) VALUES (?, ?, ?, ?, 1, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            Timestamp now = Timestamp.valueOf(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")));
            ps.setString(1, user.getId());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole().name());
            ps.setTimestamp(5, now);
            ps.setTimestamp(6, now);
            ps.executeUpdate();
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM User WHERE email = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) {
            System.err.println("Erro ao verificar email: " + e.getMessage());
        }
        return false;
    }

    public void setActiveByCompany(String companyId, boolean active) {
        String sql = """
                UPDATE User SET isActive = ?, updatedAt = NOW(3)
                WHERE id IN (SELECT userId FROM CompanyMember WHERE companyId = ?)
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setString(2, companyId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar usuários da empresa: " + e.getMessage());
        }
    }

    public User findByEmail(String email) {
        String sql = "SELECT id, email, password, role FROM User WHERE email = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role").toUpperCase())
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
        }
        return null;
    }
}
