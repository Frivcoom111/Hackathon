// Pacote "model": guarda as classes que representam os dados/entidades do sistema.
// Cada classe aqui espelha (geralmente) uma tabela do banco de dados.
package com.portal.model;

// Importa o enum Role, que define os tipos/perfis de usuário (ex.: ADMIN, ALUNO...).
import com.portal.model.enums.Role;

/**
 * Classe User (Usuário): representa uma conta de acesso ao sistema.
 *
 * É uma classe de modelo (também chamada de POJO ou "entidade"): serve apenas para
 * GUARDAR dados. Ela não tem lógica de negócio, apenas atributos (campos) e seus
 * métodos de acesso (getters e setters).
 *
 * Toda pessoa que faz login — seja administrador, aluno ou empresa — possui um User.
 */
public class User {
    // ===== ATRIBUTOS (os dados que um usuário carrega) =====
    private String id;        // Identificador único do usuário (chave primária no banco).
    private String email;     // E-mail usado para fazer login.
    private String password;  // Senha do usuário (armazenada de forma protegida/hash).
    private Role role;        // Perfil/permissão do usuário (define o que ele pode fazer).
    private boolean active;   // Indica se a conta está ativa (true) ou desativada (false).
    private Address address;  // Endereço associado ao usuário (objeto Address).

    /**
     * Construtor vazio (sem argumentos).
     * Necessário para criar um User "em branco" e ir preenchendo os campos depois,
     * normalmente usado quando lemos dados do banco linha por linha.
     */
    public User() {}

    /**
     * Construtor com os principais campos já preenchidos.
     * Facilita criar um usuário completo em uma única linha de código.
     */
    public User(String id, String email, String password, Role role) {
        this.id = id;             // "this.id" é o atributo da classe; "id" é o parâmetro recebido.
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ===== GETTERS: métodos para LER o valor de cada atributo =====
    public String getId()       { return id; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }
    public Role getRole()       { return role; }
    public boolean isActive()   { return active; }   // Para boolean, o padrão Java usa "is" no lugar de "get".
    public Address getAddress() { return address; }

    // ===== SETTERS: métodos para ALTERAR/DEFINIR o valor de cada atributo =====
    public void setId(String id)          { this.id = id; }
    public void setEmail(String email)    { this.email = email; }
    public void setPassword(String pw)    { this.password = pw; }
    public void setRole(Role role)        { this.role = role; }
    public void setActive(boolean active) { this.active = active; }
    public void setAddress(Address a)     { this.address = a; }
}
