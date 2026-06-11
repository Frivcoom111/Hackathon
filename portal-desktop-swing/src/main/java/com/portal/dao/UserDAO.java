package com.portal.dao;

import com.portal.model.User;
import com.portal.model.enums.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO extends BaseDAO {

    public User findByEmail(String email) {
        String sql = "SELECT id, email, password, role, isActive FROM User WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(Role.valueOf(rs.getString("role")));
                    user.setActive(rs.getBoolean("isActive"));
                    return user;
                }
            }

        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
        }

        return null;
    }
}
