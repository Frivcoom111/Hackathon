package com.portal.model;

// Enum que define a situação da candidatura (ex.: ABERTA, APROVADA, REJEITADA...).
import com.portal.model.enums.ApplicationStatus;

/**
 * Classe Application (Candidatura): representa a inscrição de um aluno em uma vaga.
 *
 * É uma entidade/modelo que liga um Student (aluno) a uma Job (vaga).
 *
 * Repare que, além dos IDs, ela também guarda studentName e jobTitle. Esses campos
 * "extras" são preenchidos com dados já cruzados (JOIN) do banco, para facilitar a
 * exibição em tabelas sem precisar buscar o nome do aluno e o título da vaga de novo.
 */
public class Application {
    // ===== ATRIBUTOS da candidatura =====
    private String id;                  // Identificador único da candidatura (chave primária).
    private String studentId;           // ID do aluno que se candidatou.
    private String jobId;               // ID da vaga para a qual ele se candidatou.
    private ApplicationStatus status;   // Situação da candidatura (ABERTA, APROVADA...).
    private String studentName;         // Nome do aluno (campo de apoio para exibição em tela).
    private String jobTitle;            // Título da vaga (campo de apoio para exibição em tela).

    /** Construtor vazio: cria uma candidatura "em branco". */
    public Application() {}

    /** Construtor completo: cria uma candidatura já preenchida. */
    public Application(String id, String studentId, String jobId, ApplicationStatus status,
                       String studentName, String jobTitle) {
        this.id = id;
        this.studentId = studentId;
        this.jobId = jobId;
        this.status = status;
        this.studentName = studentName;
        this.jobTitle = jobTitle;
    }

    // ===== GETTERS: leem os valores =====
    public String getId()                  { return id; }
    public String getStudentId()           { return studentId; }
    public String getJobId()               { return jobId; }
    public ApplicationStatus getStatus()   { return status; }
    public String getStudentName()         { return studentName; }
    public String getJobTitle()            { return jobTitle; }

    // ===== SETTERS: definem/alteram os valores =====
    public void setId(String id)                    { this.id = id; }
    public void setStudentId(String studentId)      { this.studentId = studentId; }
    public void setJobId(String jobId)              { this.jobId = jobId; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public void setStudentName(String studentName)  { this.studentName = studentName; }
    public void setJobTitle(String jobTitle)        { this.jobTitle = jobTitle; }
}
