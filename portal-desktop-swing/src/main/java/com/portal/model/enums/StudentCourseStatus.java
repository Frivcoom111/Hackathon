package com.portal.model.enums;

/**
 * Enum StudentCourseStatus (Status da Matrícula): lista as situações possíveis do
 * vínculo entre um aluno e um curso (StudentCourse).
 *
 * OBSERVAÇÃO: preparado para a funcionalidade de matrículas, que ainda será integrada
 * (a classe StudentCourse correspondente está reservada/vazia por enquanto).
 */
public enum StudentCourseStatus {
    ACTIVE,    // Ativa: aluno cursando/matriculado.
    COMPLETED, // Concluída: aluno terminou o curso.
    CANCELLED  // Cancelada: matrícula cancelada/trancada.
}
