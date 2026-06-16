package com.portal.model;

// Enum que define o papel do membro dentro da empresa (ex.: ADMINISTRADOR, RECRUTADOR...).
import com.portal.model.enums.CompanyMemberRole;

/**
 * Classe CompanyMember (Membro da Empresa): representa uma pessoa que trabalha em uma
 * empresa parceira e tem acesso ao portal em nome dela (ex.: um recrutador).
 *
 * É uma entidade/modelo. Liga uma pessoa (com User para login) a uma Company,
 * definindo qual papel/permissão ela tem dentro daquela empresa.
 */
public class CompanyMember {
    // ===== ATRIBUTOS do membro da empresa =====
    private String id;                // Identificador único do membro (chave primária).
    private String companyId;         // ID da empresa à qual o membro pertence.
    private String userId;            // ID da conta de usuário (User) usada para login.
    private String name;              // Nome do membro.
    private String cpf;               // CPF do membro.
    private String phone;             // Telefone de contato.
    private CompanyMemberRole role;   // Papel dentro da empresa (ADMINISTRADOR, RECRUTADOR...).
    private String email;             // E-mail do membro (campo de apoio, vindo do User).
    private boolean userActive;       // Indica se a conta de login do membro está ativa.

    /** Construtor vazio: cria um membro "em branco". */
    public CompanyMember() {}

    /** Construtor completo: cria um membro já preenchido. */
    public CompanyMember(String id, String companyId, String userId,
                         String name, String cpf, String phone,
                         CompanyMemberRole role, String email, boolean userActive) {
        this.id = id;
        this.companyId = companyId;
        this.userId = userId;
        this.name = name;
        this.cpf = cpf;
        this.phone = phone;
        this.role = role;
        this.email = email;
        this.userActive = userActive;
    }

    // ===== GETTERS: leem os valores =====
    public String getId()              { return id; }
    public String getCompanyId()       { return companyId; }
    public String getUserId()          { return userId; }
    public String getName()            { return name; }
    public String getCpf()             { return cpf; }
    public String getPhone()           { return phone; }
    public CompanyMemberRole getRole() { return role; }
    public String getEmail()           { return email; }
    public boolean isUserActive()      { return userActive; }

    // ===== SETTERS: definem/alteram os valores =====
    public void setId(String id)                        { this.id = id; }
    public void setCompanyId(String companyId)          { this.companyId = companyId; }
    public void setUserId(String userId)                { this.userId = userId; }
    public void setName(String name)                    { this.name = name; }
    public void setCpf(String cpf)                      { this.cpf = cpf; }
    public void setPhone(String phone)                  { this.phone = phone; }
    public void setRole(CompanyMemberRole role)         { this.role = role; }
    public void setEmail(String email)                  { this.email = email; }
    public void setUserActive(boolean userActive)       { this.userActive = userActive; }
}
