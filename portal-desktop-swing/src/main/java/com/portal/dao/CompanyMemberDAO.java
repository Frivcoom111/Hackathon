package com.portal.dao;

import com.portal.model.CompanyMember;
import com.portal.model.enums.CompanyMemberRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CompanyMemberDAO: DAO responsável pelos MEMBROS de uma empresa (tabela CompanyMember).
 */
public class CompanyMemberDAO extends BaseDAO {

    /**
     * Busca todos os membros de uma empresa específica, trazendo também o e-mail e a
     * situação (ativo/inativo) da conta de login de cada um (via JOIN com a tabela User).
     *
     * @param companyId ID da empresa cujos membros queremos listar.
     * @return lista de membros, ordenada por nome.
     */
    public List<CompanyMember> findByCompanyId(String companyId) {
        List<CompanyMember> list = new ArrayList<>();
        // O "?" é um parâmetro: o valor real (companyId) é preenchido depois, com segurança.
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
            ps.setString(1, companyId); // Substitui o "?" pelo ID da empresa.
            // O ResultSet é obtido dentro de outro try-with-resources para ser fechado corretamente.
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar membros da empresa: " + e.getMessage());
        }
        return list;
    }

    /** Converte uma linha do resultado em um objeto CompanyMember. */
    private CompanyMember map(ResultSet rs) throws SQLException {
        return new CompanyMember(
            rs.getString("id"),
            rs.getString("companyId"),
            rs.getString("userId"),
            rs.getString("name"),
            rs.getString("cpf"),
            rs.getString("phone"),
            CompanyMemberRole.valueOf(rs.getString("role")), // Texto do banco -> enum.
            rs.getString("email"),
            rs.getBoolean("isActive")
        );
    }
}
