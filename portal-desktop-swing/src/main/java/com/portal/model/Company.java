package com.portal.model;

// Importa o enum CompanyStatus, que define a situação da empresa (ex.: PENDENTE, APROVADA...).
import com.portal.model.enums.CompanyStatus;

/**
 * Classe Company (Empresa): representa uma empresa parceira que oferta vagas.
 *
 * É uma entidade/modelo. As empresas se cadastram, passam por aprovação
 * (controlada pelo campo status) e então podem publicar vagas de emprego/estágio.
 */
public class Company {
    // ===== ATRIBUTOS da empresa =====
    private String id;              // Identificador único da empresa (chave primária).
    private String name;            // Nome/razão social da empresa.
    private String cnpj;            // CNPJ (documento de identificação da empresa).
    private String description;     // Descrição/apresentação da empresa.
    private String phone;           // Telefone de contato.
    private CompanyStatus status;   // Situação atual (PENDENTE, APROVADA, REJEITADA...).
    private Address address;        // Endereço da empresa.

    /** Construtor vazio: cria uma empresa "em branco". */
    public Company() {}

    /** Construtor completo: cria uma empresa já com seus dados principais. */
    public Company(String id, String name, String cnpj, String description, String phone, CompanyStatus status) {
        this.id = id;
        this.name = name;
        this.cnpj = cnpj;
        this.description = description;
        this.phone = phone;
        this.status = status;
    }

    // ===== GETTERS: leem os valores =====
    public String getId()            { return id; }
    public String getName()          { return name; }
    public String getCnpj()          { return cnpj; }
    public String getDescription()   { return description; }
    public String getPhone()         { return phone; }
    public CompanyStatus getStatus() { return status; }
    public Address getAddress()      { return address; }

    // ===== SETTERS: definem/alteram os valores =====
    public void setId(String id)                  { this.id = id; }
    public void setName(String name)              { this.name = name; }
    public void setCnpj(String cnpj)              { this.cnpj = cnpj; }
    public void setDescription(String description){ this.description = description; }
    public void setPhone(String phone)            { this.phone = phone; }
    public void setStatus(CompanyStatus status)   { this.status = status; }
    public void setAddress(Address a)             { this.address = a; }
}
