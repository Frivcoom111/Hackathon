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

    // Troca/envia o currículo do aluno (PATCH multipart, campo "resume").
    public function atualizarCurriculo(array $arquivo): array
    {
        return $this->client->patchMultipart('/student/resume', [], $arquivo, true);
    }

    // Baixa o currículo do próprio aluno autenticado (binário).
    public function baixarCurriculo(): array
    {
        return $this->client->downloadRaw('/student/resume/download', true);
    }
}
