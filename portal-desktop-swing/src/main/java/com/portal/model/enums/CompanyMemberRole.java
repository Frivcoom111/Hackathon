package com.portal.model.enums;

/**
 * Enum CompanyMemberRole (Papel do Membro da Empresa): define a função de uma pessoa
 * dentro de uma empresa parceira.
 *
 * Controla o nível de permissão do membro (CompanyMember) dentro da sua empresa.
 */
public enum CompanyMemberRole {
    ADMIN,      // Administrador da empresa: gerencia a empresa e seus outros membros.
    RECRUITER   // Recrutador: publica vagas e avalia candidaturas.
}
