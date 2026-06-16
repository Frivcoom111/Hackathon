package com.portal.model;

// LocalDateTime: tipo do Java que representa data + hora (ex.: 16/06/2026 14:30).
import java.time.LocalDateTime;

/**
 * Classe Course (Curso): representa um curso oferecido pela instituição.
 *
 * É uma entidade/modelo. Cada aluno está vinculado a um curso, e as vagas podem
 * ser direcionadas a determinados cursos.
 */
public class Course {
    // ===== ATRIBUTOS do curso =====
    private String id;                  // Identificador único do curso (chave primária).
    private String name;                // Nome do curso (ex.: "Análise e Desenvolvimento de Sistemas").
    private String code;                // Código/sigla do curso (ex.: "ADS").
    private int periods;                // Quantidade de períodos/semestres do curso.
    private boolean active;             // Indica se o curso está ativo (true) ou desativado.
    private LocalDateTime createdAt;    // Data e hora em que o curso foi criado no sistema.
    private LocalDateTime updatedAt;    // Data e hora da última atualização do curso.

    /** Construtor vazio: cria um curso "em branco". */
    public Course() {}

    /** Construtor completo: cria um curso já preenchido. */
    public Course(String id, String name, String code, int periods, boolean active,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.periods = periods;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ===== GETTERS: leem os valores =====
    public String getId()               { return id; }
    public String getName()             { return name; }
    public String getCode()             { return code; }
    public int getPeriods()             { return periods; }
    public boolean isActive()           { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ===== SETTERS: definem/alteram os valores =====
    public void setId(String id)                      { this.id = id; }
    public void setName(String name)                  { this.name = name; }
    public void setCode(String code)                  { this.code = code; }
    public void setPeriods(int periods)               { this.periods = periods; }
    public void setActive(boolean active)             { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
