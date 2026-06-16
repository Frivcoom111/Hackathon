package com.portal.dao;

import com.portal.model.User;
import com.portal.model.enums.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO: DAO responsável por acessar os dados dos USUÁRIOS (tabela User).
 *
 * Cuida de salvar novos usuários, verificar duplicidade de e-mail, listar usuários,
 * (des)ativar contas e buscar um usuário pelo e-mail (usado no login).
 */
public class UserDAO extends BaseDAO {

    /**
     * Insere um novo usuário no banco.
     *
     * IMPORTANTE: este método recebe a conexão (conn) de fora, em vez de abrir a sua
     * própria. Isso permite que o cadastro do usuário faça parte de uma TRANSAÇÃO maior
     * (ex.: criar User + Student juntos), garantindo que tudo seja salvo ou nada seja.
     */
    public void save(User user, Connection conn) throws Exception {
        // O valor "1" fixo em isActive significa que todo usuário já nasce ativo.
        String sql = "INSERT INTO User (id, email, password, role, isActive, createdAt, updatedAt) VALUES (?, ?, ?, ?, 1, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            Timestamp now = now(); // Mesma data/hora para criação e atualização.
            ps.setString(1, user.getId());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());     // Já deve vir como hash.
            ps.setString(4, user.getRole().name());  // Enum -> texto.
            ps.setTimestamp(5, now);
            ps.setTimestamp(6, now);
            ps.executeUpdate();
        }
    }

    /**
     * Verifica se já existe um usuário com o e-mail informado.
     * Usado para impedir cadastros duplicados.
     *
     * @return true se o e-mail já estiver em uso; false caso contrário.
     */
    public boolean existsByEmail(String email) {
        // "SELECT 1 ... LIMIT 1": só queremos saber SE existe, não os dados; por isso é rápido.
        String sql = "SELECT 1 FROM User WHERE email = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            // Se rs.next() for true, é porque achou uma linha => o e-mail existe.
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (Exception e) {
            System.err.println("Erro ao verificar email: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lista todos os usuários, juntando o endereço quando o usuário for um aluno.
     * Ordena por perfil (role) e depois por e-mail.
     */
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        // Dois LEFT JOINs: User -> Student -> Address. Só alunos terão endereço aqui;
        // para os demais, as colunas de endereço virão nulas.
        String sql = """
                SELECT u.id, u.email, u.role, u.isActive,
                       a.id AS addressId, a.street, a.number, a.complement,
                       a.district, a.city, a.state, a.zipCode
                FROM User u
                LEFT JOIN Student s ON s.userId = u.id
                LEFT JOIN Address a ON a.id = s.addressId
                ORDER BY u.role, u.email
                """;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapFull(rs));
        } catch (Exception e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
        }
        return list;
    }

    /**
     * Converte uma linha em um User "completo" (com endereço), porém SEM a senha.
     * Repare no "null" no lugar da senha: por segurança, não trazemos o hash em listagens.
     */
    private User mapFull(ResultSet rs) throws Exception {
        User u = new User(
            rs.getString("id"),
            rs.getString("email"),
            null, // senha não é carregada nas listagens.
            Role.valueOf(rs.getString("role").toUpperCase()) // toUpperCase por segurança ao converter.
        );
        u.setActive(rs.getBoolean("isActive"));
        u.setAddress(mapAddress(rs));
        return u;
    }

    /**
     * Ativa ou desativa, de uma só vez, TODOS os usuários ligados a uma empresa.
     * Usado quando uma empresa é bloqueada/desbloqueada — todos os seus membros
     * perdem ou recuperam o acesso junto.
     *
     * NOW(3) é uma função do próprio banco que devolve a data/hora atual.
     */
    public void setActiveByCompany(String companyId, boolean active) {
        // A subconsulta seleciona os IDs de usuário de todos os membros daquela empresa.
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

    /**
     * Busca um usuário pelo e-mail, trazendo a SENHA (hash).
     * Este é o método usado no LOGIN: precisamos do hash para comparar com a senha digitada.
     *
     * @return o usuário encontrado (com senha), ou null se o e-mail não existir.
     */
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
                        rs.getString("password"), // Aqui a senha (hash) É carregada, para o login.
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
