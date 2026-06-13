package com.portal.dao;

import com.portal.model.CompanyMember;
import com.portal.model.enums.CompanyMemberRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyMemberDAO extends BaseDAO {

    public List<CompanyMember> findByCompanyId(String companyId) {
        List<CompanyMember> list = new ArrayList<>();
        String sql = """
                SELECT cm.id, cm.companyId, cm.userId, cm.name, cm.cpf, cm.phone, cm.role,
                       u.email, u.isActive
                FROM CompanyMember cm
                JOIN User u ON u.id = cm.userId
                WHERE cm.companyId = ?
                ORDER BY cm.name
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar membros da empresa: " + e.getMessage());
        }
        return list;
    }

    private CompanyMember map(ResultSet rs) throws SQLException {
        return new CompanyMember(
            rs.getString("id"),
            rs.getString("companyId"),
            rs.getString("userId"),
            rs.getString("name"),
            rs.getString("cpf"),
            rs.getString("phone"),
            CompanyMemberRole.valueOf(rs.getString("role")),
            rs.getString("email"),
            rs.getBoolean("isActive")
        );
    }
}
