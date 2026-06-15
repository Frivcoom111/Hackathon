<?php

namespace App\Auth;

class JwtManager
{
    public function save(string $token): void
    {
        $_SESSION['token'] = $token;
    }

    public function get(): ?string
    {
        return $_SESSION['token'] ?? null;
    }

    public function hasToken(): bool
    {
        return !empty($_SESSION['token']);
    }

    public function destroy(): void
    {
        unset($_SESSION['token'], $_SESSION['tempToken'], $_SESSION['role']);
    }

    public function saveTempToken(string $token): void
    {
        $_SESSION['tempToken'] = $token;
    }

    public function getTempToken(): ?string
    {
        return $_SESSION['tempToken'] ?? null;
    }

    public function getPayload(): array
    {
        $token = $this->get();
        if (!$token) {
            return [];
        }

        $parts = explode('.', $token);
        if (count($parts) !== 3) {
            return [];
        }

        $payload = base64_decode(str_pad(
            strtr($parts[1], '-_', '+/'),
            strlen($parts[1]) % 4 === 0 ? strlen($parts[1]) : strlen($parts[1]) + (4 - strlen($parts[1]) % 4),
            '='
        ));

        return json_decode($payload, true) ?? [];
    }

    public function getRole(): ?string
    {
        $payload = $this->getPayload();
        return $payload['role'] ?? null;
    }

    public function isStudent(): bool
    {
        return $this->getRole() === 'STUDENT';
    }

    public function isCompany(): bool
    {
        return $this->getRole() === 'COMPANY';
    }

    public function isAdmin(): bool
    {
        return $this->getRole() === 'ADMIN';
    }

    /**
     * Papel do membro DENTRO da empresa (ADMIN | RECRUITER).
     * Vem na JWT apenas quando role === COMPANY. Não confundir com getRole().
     */
    public function getCompanyMemberRole(): ?string
    {
        $payload = $this->getPayload();
        return $payload['companyMemberRole'] ?? null;
    }

    public function isCompanyAdmin(): bool
    {
        return $this->isCompany() && $this->getCompanyMemberRole() === 'ADMIN';
    }

    public function isCompanyRecruiter(): bool
    {
        return $this->isCompany() && $this->getCompanyMemberRole() === 'RECRUITER';
    }

    public function isMfaVerified(): bool
    {
        return (bool)($this->getPayload()['mfaVerified'] ?? false);
    }

    /**
     * "Totalmente autenticado": tem token E, no caso de empresa, já passou
     * pelo MFA. Durante o fluxo TOTP o tempToken fica salvo (mfaVerified=false),
     * então hasToken() sozinho não basta para liberar o acesso.
     */
    public function isAuthenticated(): bool
    {
        if (!$this->hasToken()) {
            return false;
        }
        return !$this->isCompany() || $this->isMfaVerified();
    }
}
