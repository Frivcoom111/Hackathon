package com.portal.dao;

import com.portal.model.Student;
import com.portal.model.User;
import com.portal.model.enums.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * StudentDAO: DAO responsável por acessar os dados dos ALUNOS (tabela Student).
 *
 * Destaque: o método saveWithUser demonstra o uso de TRANSAÇÃO — criar o aluno e a
 * sua conta de usuário "tudo ou nada", para o banco nunca ficar pela metade.
 */
public class StudentDAO extends BaseDAO {

    // O StudentDAO reutiliza o UserDAO para salvar a conta de login do aluno.
    private final UserDAO userDAO = new UserDAO();

    /** Lista todos os alunos, com e-mail (do User) e endereço (se houver), ordenados por nome. */
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        // JOIN com User (todo aluno tem usuário) e LEFT JOIN com Address (endereço é opcional).
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

    /**
     * Busca alunos por um TERMO de pesquisa, comparando com o nome OU o RA.
     *
     * Usa o operador LIKE com "%": "%termo%" encontra o termo em qualquer parte do texto.
     * Ex.: term = "ana" encontra "Mariana", "Ana", "Adriana"...
     */
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
            String like = "%" + term + "%"; // Monta o padrão de busca parcial.
            ps.setString(1, like); // Para o nome.
            ps.setString(2, like); // Para o RA.
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar alunos: " + e.getMessage());
        }
        return list;
    }

    /**
     * Verifica se já existe OUTRO aluno com o mesmo RA.
     * excludeId permite ignorar o próprio aluno ao editar (passa null ao criar).
     */
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

    /**
     * Verifica se já existe OUTRO aluno com o mesmo CPF.
     * Mesma lógica do existsByRa, mas comparando o CPF.
     */
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

    /**
     * Cria um aluno JUNTO com a sua conta de usuário, usando uma TRANSAÇÃO.
     *
     * O QUE É UMA TRANSAÇÃO? É um conjunto de operações que devem acontecer "tudo ou nada".
     * Aqui precisamos inserir um User E um Student. Se o segundo INSERT falhar, não podemos
     * deixar o primeiro salvo (sobraria um usuário sem aluno). A transação garante isso:
     *
     *   - setAutoCommit(false): desliga o salvamento automático; nada é gravado de verdade ainda.
     *   - commit(): confirma TODAS as operações de uma vez (se tudo deu certo).
     *   - rollback(): desfaz TUDO (se qualquer passo falhar).
     *   - finally + setAutoCommit(true): restaura o comportamento normal da conexão.
     *
     * Observação: a senha inicial do aluno é o próprio CPF (passado como senha do User).
     */
    public void saveWithUser(Student student) throws Exception {
        // Gera ids únicos para o usuário e para o aluno.
        String userId = UUID.randomUUID().toString();
        String studentId = UUID.randomUUID().toString();
        student.setUserId(userId);
        student.setId(studentId);

        // Cria a conta de login do aluno (perfil STUDENT; senha inicial = CPF).
        User user = new User(userId, student.getEmail(), student.getCpf(), Role.STUDENT);

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Inicia a transação (desliga o commit automático).
            try {
                userDAO.save(user, conn);      // 1) Insere o usuário (na mesma conexão).
                insertStudent(student, conn);  // 2) Insere o aluno (na mesma conexão).
                conn.commit();                 // Deu tudo certo: confirma as duas inserções.
            } catch (Exception e) {
                conn.rollback();               // Algo falhou: desfaz tudo que foi feito.
                throw e;                        // Repassa o erro para quem chamou.
            } finally {
                conn.setAutoCommit(true);      // Restaura o modo automático da conexão.
            }
        }
    }

    /** Atualiza os dados editáveis de um aluno já existente. */
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

    /**
     * Alterna o aluno entre apto/inapto (campo isEligible).
     * "isEligible = NOT isEligible" faz o banco inverter o valor atual.
     */
    public void toggleEligible(String id) throws Exception {
        String sql = "UPDATE Student SET isEligible = NOT isEligible, updatedAt = NOW() WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Insere a linha do aluno na tabela Student.
     * É "private" e recebe a conexão de fora porque faz parte da transação de saveWithUser.
     * O valor "1" fixo em isEligible faz o aluno já nascer apto.
     */
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

    /** Converte uma linha do resultado em um objeto Student, incluindo o endereço. */
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
