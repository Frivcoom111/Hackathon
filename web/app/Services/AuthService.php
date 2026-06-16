<?php

namespace App\Services;

class AuthService extends BaseService
{
    public function login(string $email, string $senha): array
    {
        return $this->client->post('/auth/login', [
            'email'    => $email,
            'password' => $senha,
        ]);
    }

    public function me(): array
    {
        return $this->client->get('/auth/me', [], true);
    }

    public function totpSetupConfirm(string $code): array
    {
        return $this->client->post('/auth/totp/setup/confirm', ['code' => $code], true);
    }

    public function totpVerify(string $code): array
    {
        return $this->client->post('/auth/totp/verify', ['code' => $code], true);
    }

    public function registrarEstudante(array $dados): array
    {
        return $this->client->post('/auth/register/student', $dados);
    }

    public function registrarEmpresa(array $dados): array
    {
        return $this->client->post('/auth/register/company', $dados);
    }
}
