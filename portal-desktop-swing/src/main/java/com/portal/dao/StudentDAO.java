package com.portal.dao;

import com.portal.model.Student;
import com.portal.model.User;
import com.portal.model.enums.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StudentDAO extends BaseDAO {

    private final UserDAO userDAO = new UserDAO();

    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = """
                SELECT s.id, s.userId, s.name, s.ra, s.cpf, s.phone, s.isEligible,
                       u.email,
                       a.id AS addressId, a.street, a.number, a.complement,
                       a.district, a.city, a.state, a.zipCode
                FROM Student s
                JOIN User u ON u.id = s.userId
                LEFT JOIN Address a ON a.id = s.addressId
                ORDER BY s.name
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (Exception e) {
            System.err.println("Erro ao listar alunos: " + e.getMessage());
        }
        return list;
    }

    public List<Student> findByTerm(String term) {
        List<Student> list = new ArrayList<>();
        String sql = """
                SELECT s.id, s.userId, s.name, s.ra, s.cpf, s.phone, s.isEligible,
                       u.email,
                       a.id AS addressId, a.street, a.number, a.complement,
                       a.district, a.city, a.state, a.zipCode
                FROM Student s
                JOIN User u ON u.id = s.userId
                LEFT JOIN Address a ON a.id = s.addressId
                WHERE s.name LIKE ? OR s.ra LIKE ?
                ORDER BY s.name
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + term + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar alunos: " + e.getMessage());
        }
        return list;
    }

    public boolean existsByRa(String ra, String excludeId) {
        String sql = "SELECT 1 FROM Student WHERE ra = ? AND id != ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ra);
            ps.setString(2, excludeId == null ? "" : excludeId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { System.err.println("Erro ao verificar RA: " + e.getMessage()); }
        return false;
    }

    public boolean existsByCpf(String cpf, String excludeId) {
        String sql = "SELECT 1 FROM Student WHERE cpf = ? AND id != ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cpf);
            ps.setString(2, excludeId == null ? "" : excludeId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) { System.err.println("Erro ao verificar CPF: " + e.getMessage()); }
        return false;
    }

    public void saveWithUser(Student student) throws Exception {
        String userId = UUID.randomUUID().toString();
        String studentId = UUID.randomUUID().toString();
        student.setUserId(userId);
        student.setId(studentId);

        User user = new User(userId, student.getEmail(), student.getCpf(), Role.STUDENT);

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                userDAO.save(user, conn);
                insertStudent(student, conn);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public void update(Student student) throws Exception {
        String sql = "UPDATE Student SET name = ?, ra = ?, cpf = ?, phone = ?, updatedAt = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getRa());
            ps.setString(3, student.getCpf());
            ps.setString(4, student.getPhone());
            ps.setTimestamp(5, now());
            ps.setString(6, student.getId());
            ps.executeUpdate();
        }
    }

    public void toggleEligible(String id) throws Exception {
        String sql = "UPDATE Student SET isEligible = NOT isEligible, updatedAt = NOW() WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    private void insertStudent(Student student, Connection conn) throws Exception {
        String sql = """
                INSERT INTO Student (id, userId, name, ra, cpf, phone, isEligible, createdAt, updatedAt)
                VALUES (?, ?, ?, ?, ?, ?, 1, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            Timestamp now = now();
            ps.setString(1, student.getId());
            ps.setString(2, student.getUserId());
            ps.setString(3, student.getName());
            ps.setString(4, student.getRa());
            ps.setString(5, student.getCpf());
            ps.setString(6, student.getPhone());
            ps.setTimestamp(7, now);
            ps.setTimestamp(8, now);
            ps.executeUpdate();
        }
    }

    private Student map(ResultSet rs) throws SQLException {
        Student s = new Student(
            rs.getString("id"),
            rs.getString("userId"),
            rs.getString("name"),
            rs.getString("ra"),
            rs.getString("cpf"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getBoolean("isEligible")
        );
        s.setAddress(mapAddress(rs));
        return s;
    }
}
