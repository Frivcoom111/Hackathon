package com.portal.model;

// Enums que descrevem a vaga: modalidade (presencial/remoto...) e status (aberta/fechada...).
import com.portal.model.enums.JobModality;
import com.portal.model.enums.JobStatus;

// BigDecimal: tipo ideal para valores monetários, pois evita erros de arredondamento
// que aconteceriam usando double/float para dinheiro.
import java.math.BigDecimal;

/**
 * Classe Job (Vaga): representa uma vaga de emprego/estágio publicada por uma empresa.
 *
 * É uma entidade/modelo. Os alunos se candidatam a uma Job, e essa candidatura
 * é representada pela classe Application.
 */
public class Job {
    // ===== ATRIBUTOS da vaga =====
    private String id;            // Identificador único da vaga (chave primária).
    private String title;         // Título da vaga (ex.: "Desenvolvedor Java Júnior").
    private String area;          // Área de atuação (ex.: "Tecnologia", "Financeiro").
    private String location;      // Localização da vaga (cidade/região).
    private JobModality modality; // Modalidade de trabalho (PRESENCIAL, REMOTO, HÍBRIDO...).
    private JobStatus status;     // Situação da vaga (ABERTA, FECHADA...).
    private BigDecimal salary;    // Salário oferecido (valor monetário).

    /** Construtor vazio: cria uma vaga "em branco". */
    public Job() {}

    /** Construtor completo: cria uma vaga já preenchida. */
    public Job(String id, String title, String area, String location,
               JobModality modality, JobStatus status, BigDecimal salary) {
        this.id = id;
        this.title = title;
        this.area = area;
        this.location = location;
        this.modality = modality;
        this.status = status;
        this.salary = salary;
    }

    // ===== GETTERS: leem os valores =====
    public String getId()           { return id; }
    public String getTitle()        { return title; }
    public String getArea()         { return area; }
    public String getLocation()     { return location; }
    public JobModality getModality(){ return modality; }
    public JobStatus getStatus()    { return status; }
    public BigDecimal getSalary()   { return salary; }

    // ===== SETTERS: definem/alteram os valores =====
    public void setId(String id)                { this.id = id; }
    public void setTitle(String title)          { this.title = title; }
    public void setArea(String area)            { this.area = area; }
    public void setLocation(String location)    { this.location = location; }
    public void setModality(JobModality m)      { this.modality = m; }
    public void setStatus(JobStatus s)          { this.status = s; }
    public void setSalary(BigDecimal salary)    { this.salary = salary; }
}
