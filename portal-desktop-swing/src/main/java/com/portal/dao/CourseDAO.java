package com.portal.dao;

import com.portal.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // Gera identificadores únicos (ids) para novos cursos.

/**
 * CourseDAO: DAO responsável por acessar os dados dos CURSOS (tabela Course).
 *
 * Implementa o CRUD do curso: criar (save), ler (findAll/findById), atualizar (update)
 * e ativar/desativar (toggleActive). Também verifica duplicidade de nome (existsByName).
 */
public class CourseDAO extends BaseDAO {

    /** Lista todos os cursos, em ordem alfabética de nome. */
    public List<Course> findAll() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT id, name, code, periods, isActive, createdAt, updatedAt FROM Course ORDER BY name";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) {
            System.err.println("Erro ao listar cursos: " + e.getMessage());
        }
        return list;
    }

    /**
     * Busca um curso pelo id.
     * @return o curso, ou null se não existir.
     */
    public Course findById(String id) {
        String sql = "SELECT id, name, code, periods, isActive, createdAt, updatedAt FROM Course WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar curso: " + e.getMessage());
        }
        return null;
    }

    /**
     * Verifica se já existe OUTRO curso com o mesmo nome.
     *
     * O parâmetro excludeId serve para, ao EDITAR um curso, ignorar ele mesmo na checagem
     * (senão o curso seria acusado de duplicar o próprio nome). Ao criar um novo, passa-se null.
     *
     * @return true se o nome já estiver em uso por outro curso.
     */
    public boolean existsByName(String name, String excludeId) {
        String sql = "SELECT 1 FROM Course WHERE name = ? AND id != ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            // Se excludeId for null (caso de criação), usa "" para não excluir nenhum registro.
            ps.setString(2, excludeId == null ? "" : excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar nome: " + e.getMessage());
        }
        return false;
    }

    /**
     * Salva (insere) um novo curso no banco.
     * O id é gerado aqui mesmo, com UUID (identificador universal único).
     */
    public void save(Course course) {
        course.setId(UUID.randomUUID().toString()); // Gera um id único para o novo curso.
        String sql = "INSERT INTO Course (id, name, code, periods, isActive, createdAt, updatedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            Timestamp now = now();
            ps.setString(1, course.getId());
            ps.setString(2, course.getName());
            ps.setString(3, course.getCode());
            ps.setInt(4, course.getPeriods());
            ps.setBoolean(5, true); // Curso novo já entra ativo.
            ps.setTimestamp(6, now);
            ps.setTimestamp(7, now);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Erro ao salvar curso: " + e.getMessage());
            throw new RuntimeException("Erro ao salvar curso.", e); // Relança para a camada de cima.
        }
    }

    /** Atualiza nome, código e períodos de um curso existente (identificado pelo id). */
    public void update(Course course) {
        String sql = "UPDATE Course SET name = ?, code = ?, periods = ?, updatedAt = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            Timestamp now = now();
            ps.setString(1, course.getName());
            ps.setString(2, course.getCode());
            ps.setInt(3, course.getPeriods());
            ps.setTimestamp(4, now);
            ps.setString(5, course.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar curso: " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar curso.", e);
        }
    }

    /**
     * Inverte o status ativo/inativo do curso.
     * O truque "isActive = NOT isActive" deixa o próprio banco alternar o valor:
     * se estava ativo, fica inativo, e vice-versa.
     */
    public void toggleActive(String id) {
        String sql = "UPDATE Course SET isActive = NOT isActive, updatedAt = NOW() WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Erro ao alterar status: " + e.getMessage());
            throw new RuntimeException("Erro ao alterar status do curso.", e);
        }
    }

    /**
     * Converte uma linha do resultado em um objeto Course.
     * As datas vêm como Timestamp do banco e são convertidas para LocalDateTime do Java
     * (com proteção contra valor nulo).
     */
    private Course map(ResultSet rs) throws SQLException {
        Timestamp createdAt = rs.getTimestamp("createdAt");
        Timestamp updatedAt = rs.getTimestamp("updatedAt");
        return new Course(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("code"),
            rs.getInt("periods"),
            rs.getBoolean("isActive"),
            createdAt != null ? createdAt.toLocalDateTime() : null,
            updatedAt != null ? updatedAt.toLocalDateTime() : null
        );
    }
}
