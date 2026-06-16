package com.portal.model.enums;

/**
 * Enum CompanyStatus (Status da Empresa): lista as situações possíveis de uma empresa
 * (Company) dentro do processo de cadastro e aprovação.
 *
 * Uma empresa só pode atuar plenamente (publicar vagas) depois de ser APPROVED.
 */
public enum CompanyStatus {
    PENDING,    // Pendente: cadastro feito, aguardando análise do administrador.
    ANALYSING,  // Em análise: o administrador está avaliando o cadastro.
    APPROVED,   // Aprovada: empresa liberada para usar o sistema.
    BLOCKED     // Bloqueada: empresa impedida de atuar (reprovada ou suspensa).
}
