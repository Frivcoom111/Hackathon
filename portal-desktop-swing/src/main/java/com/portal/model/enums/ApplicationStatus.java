package com.portal.model.enums;

/**
 * Enum ApplicationStatus (Status da Candidatura): lista os estados possíveis de uma
 * candidatura (Application) de um aluno a uma vaga.
 *
 * Representa o "ciclo de vida" da candidatura, do envio até a decisão final.
 */
public enum ApplicationStatus {
    PENDING,    // Pendente: candidatura enviada, aguardando análise.
    ANALYSING,  // Em análise: a empresa está avaliando o candidato.
    APPROVED,   // Aprovada: o aluno foi aceito na vaga.
    REJECTED,   // Rejeitada: o aluno não foi aceito.
    CANCELLED   // Cancelada: a candidatura foi cancelada (pelo aluno ou pelo sistema).
}
