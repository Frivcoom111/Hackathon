// Pacote "enums": guarda os tipos enumerados, que são listas fixas de valores possíveis.
package com.portal.model.enums;

/**
 * Enum Role (Perfil): define os tipos de usuário que existem no sistema.
 *
 * Um "enum" é uma lista FIXA de valores possíveis. Em vez de usar textos soltos como
 * "admin" ou "aluno" (que podem ser digitados errado), usamos estes nomes garantidos
 * pelo compilador. Cada usuário (User) tem exatamente um destes perfis.
 */
public enum Role {
    ADMIN,    // Administrador: tem acesso total ao sistema (gerencia tudo).
    COMPANY,  // Empresa: representa um membro de empresa parceira.
    STUDENT   // Aluno: estudante que se candidata às vagas.
}
