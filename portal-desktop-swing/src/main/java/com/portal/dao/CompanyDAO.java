package com.portal.dao;

import com.portal.model.Company;
import com.portal.model.enums.CompanyStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CompanyDAO: DAO responsável por acessar os dados das EMPRESAS (tabela Company).
 *
 * Oferece consultas (listar todas, filtrar por status, buscar por id) e a atualização
 * do status da empresa (usado no fluxo de aprovação/bloqueio).
 */
public class CompanyDAO extends BaseDAO {

    // Trecho de SELECT reutilizado por várias consultas. Evita repetir o mesmo SQL.
    // LEFT JOIN com Address: traz o endereço SE existir; se não houver, as colunas
    // de endereço vêm nulas (e mapAddress devolve null).
    private static final String SELECT_BASE = """
            SELECT c.id, c.name, c.cnpj, c.description, c.phone, c.status,
                   a.id AS addressId, a.street, a.number, a.complement,
                   a.district, a.city, a.state, a.zipCode
            FROM Company c
            LEFT JOIN Address a ON a.id = c.addressId
            """;

    /** Lista todas as empresas, ordenadas por nome. */
    public List<Company> findAll() {
        List<Company> list = new ArrayList<>();
        String sql = SELECT_BASE + "ORDER BY c.name"; // Reaproveita o SELECT base + ordenação.
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) {
            System.err.println("Erro ao listar empresas: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lista apenas as empresas que estão em um determinado status.
     * Útil, por exemplo, para mostrar só as empresas PENDING (aguardando aprovação).
     */
    public List<Company> findByStatus(CompanyStatus status) {
        List<Company> list = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE c.status = ? ORDER BY c.name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name()); // .name() devolve o texto do enum (ex.: "PENDING").
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (Exception e) {
            System.err.println("Erro ao filtrar empresas: " + e.getMessage());
        }
        return list;
    }

    /**
     * Busca UMA empresa pelo seu id.
     * @return a empresa encontrada, ou null se não existir.
     */
    public Company findById(String id) {
        String sql = SELECT_BASE + "WHERE c.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs); // Só esperamos uma linha.
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar empresa: " + e.getMessage());
        }
        return null;
    }

    /**
     * Atualiza o status de uma empresa (ex.: aprovar ou bloquear) e registra a data
     * da alteração no campo updatedAt.
     *
     * Diferente das consultas, aqui um erro RELANÇA uma exceção (RuntimeException),
     * pois uma atualização que falhou silenciosamente poderia enganar o usuário.
     */
    public void updateStatus(String id, CompanyStatus status) {
        String sql = "UPDATE Company SET status = ?, updatedAt = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name()); // Novo status.
            ps.setTimestamp(2, now());      // Data/hora atual (do BaseDAO).
            ps.setString(3, id);            // Qual empresa atualizar.
            ps.executeUpdate();             // executeUpdate é usado para INSERT/UPDATE/DELETE.
        } catch (Exception e) {
            System.err.println("Erro ao atualizar status da empresa: " + e.getMessage());
            // Relança como exceção não-verificada para a camada de cima saber que falhou.
            throw new RuntimeException("Erro ao atualizar status da empresa.", e);
        }
    }

    /** Converte uma linha do resultado em um objeto Company, incluindo o endereço. */
    private Company map(ResultSet rs) throws SQLException {
        Company c = new Company(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("cnpj"),
            rs.getString("description"),
            rs.getString("phone"),
            CompanyStatus.valueOf(rs.getString("status"))
        );
        c.setAddress(mapAddress(rs)); // Reaproveita o mapeamento de endereço do BaseDAO.
        return c;
    }
}
