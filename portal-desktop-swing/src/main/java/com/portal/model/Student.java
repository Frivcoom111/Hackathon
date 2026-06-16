package com.portal.model;

/**
 * Classe Student (Aluno): representa um estudante cadastrado no portal.
 *
 * É uma entidade/modelo: guarda os dados de um aluno que pode se candidatar a vagas.
 * Cada aluno está ligado a um User (conta de login) pelo campo userId.
 */
public class Student {
    // ===== ATRIBUTOS do aluno =====
    private String id;        // Identificador único do aluno (chave primária).
    private String userId;    // Liga este aluno à conta de usuário (User) correspondente.
    private String name;      // Nome completo do aluno.
    private String ra;        // RA = Registro Acadêmico (matrícula do aluno na instituição).
    private String cpf;       // CPF do aluno (documento de identificação).
    private String email;     // E-mail de contato do aluno.
    private String phone;     // Telefone de contato.
    private boolean eligible; // Indica se o aluno está APTO (true) a se candidatar a vagas.
    private Address address;  // Endereço do aluno. Observação: a relação aluno↔endereço é 1:1.

    /** Construtor vazio: cria um aluno "em branco" para preencher depois. */
    public Student() {}

    /** Construtor completo: cria um aluno já com seus dados principais. */
    public Student(String id, String userId, String name, String ra, String cpf,
                   String email, String phone, boolean eligible) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.ra = ra;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        this.eligible = eligible;
    }

    // ===== GETTERS: leem os valores dos atributos =====
    public String getId()       { return id; }
    public String getUserId()   { return userId; }
    public String getName()     { return name; }
    public String getRa()       { return ra; }
    public String getCpf()      { return cpf; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
    public boolean isEligible() { return eligible; }
    public Address getAddress() { return address; }

    // ===== SETTERS: definem/alteram os valores dos atributos =====
    public void setId(String id)          { this.id = id; }
    public void setUserId(String userId)  { this.userId = userId; }
    public void setName(String name)      { this.name = name; }
    public void setRa(String ra)          { this.ra = ra; }
    public void setCpf(String cpf)        { this.cpf = cpf; }
    public void setEmail(String email)    { this.email = email; }
    public void setPhone(String phone)    { this.phone = phone; }
    public void setEligible(boolean e)    { this.eligible = e; }
    public void setAddress(Address a)     { this.address = a; }
}
