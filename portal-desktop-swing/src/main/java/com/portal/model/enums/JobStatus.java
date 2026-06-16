package com.portal.model.enums;

/**
 * Enum JobStatus (Status da Vaga): lista as situações possíveis de uma vaga (Job).
 *
 * Controla se a vaga está recebendo candidaturas ou não.
 */
public enum JobStatus {
    ACTIVE,  // Ativa: vaga aberta, recebendo candidaturas.
    PAUSED,  // Pausada: temporariamente fora do ar (não recebe candidaturas).
    CLOSED   // Fechada: vaga encerrada (preenchida ou cancelada).
}
