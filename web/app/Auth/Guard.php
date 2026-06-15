<?php

namespace App\Auth;

/**
 * Guardas de acesso reutilizáveis. Centralizam o redirect + exit que antes
 * estava espalhado em cada página. Sempre recebem o JwtManager para validar
 * a JWT (role / companyMemberRole) e decidir o destino.
 *
 * Papéis (validados no backend — auth.middlewares.ts):
 *   - role:              STUDENT | COMPANY | ADMIN (ADMIN = back-office Java)
 *   - companyMemberRole: ADMIN | RECRUITER (só quando role === COMPANY)
 */
class Guard
{
    private static function redirect(string $page): void
    {
        header('Location: ' . BASE . 'index.php?page=' . $page);
        exit;
    }

    /** Qualquer rota protegida: precisa estar totalmente autenticado (empresa com MFA). */
    public static function requireLogin(JwtManager $jwt): void
    {
        if (!$jwt->isAuthenticated()) {
            self::redirect('login');
        }
    }

    /** Área do aluno. Empresa logada é mandada para o painel dela. */
    public static function requireStudent(JwtManager $jwt): void
    {
        self::requireLogin($jwt);
        if (!$jwt->isStudent()) {
            self::redirect($jwt->isCompany() ? 'empresa-dashboard' : 'home');
        }
    }

    /** Área da empresa. Aluno logado é mandado para o perfil dele. */
    public static function requireCompany(JwtManager $jwt): void
    {
        self::requireLogin($jwt);
        if (!$jwt->isCompany()) {
            self::redirect($jwt->isStudent() ? 'perfil' : 'home');
        }
    }

    /** Ações restritas ao ADMIN da empresa (perfil da empresa, membros). */
    public static function requireCompanyAdmin(JwtManager $jwt): void
    {
        self::requireCompany($jwt);
        if (!$jwt->isCompanyAdmin()) {
            self::redirect('empresa-dashboard');
        }
    }
}
