<?php

namespace App\Services;

class EstudanteService extends BaseService
{
    public function perfil(): array
    {
        return $this->client->get('/student/profile', [], true);
    }

    public function atualizarPerfil(array $dados): array
    {
        return $this->client->patch('/student/profile', $dados, true);
    }

    public function alterarSenha(string $senhaAtual, string $novaSenha, string $confirmacao): array
    {
        return $this->client->patch('/student/password', [
            'currentPassword' => $senhaAtual,
            'newPassword'     => $novaSenha,
            'confirmPassword' => $confirmacao,
        ], true);
    }

    public function endereco(): array
    {
        return $this->client->get('/address/me', [], true);
    }

    public function salvarEndereco(array $dados, bool $atualizar = false): array
    {
        if ($atualizar) {
            return $this->client->patch('/address/me', $dados, true);
        }
        return $this->client->post('/address/me', $dados, true);
    }
}
