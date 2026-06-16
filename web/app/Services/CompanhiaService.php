<?php

namespace App\Services;

class CompanhiaService extends BaseService
{
    public function perfil(): array
    {
        return $this->client->get('/company/profile', [], true);
    }

    public function atualizarPerfil(array $dados): array
    {
        return $this->client->patch('/company/profile', $dados, true);
    }

    public function vagas(array $filtros = []): array
    {
        return $this->client->get('/company/jobs', array_merge(['page' => 1, 'limit' => 50], $filtros), true);
    }

    public function vaga(int|string $id): array
    {
        return $this->client->get("/company/jobs/{$id}", [], true);
    }

    public function criarVaga(array $dados): array
    {
        return $this->client->post('/company/jobs', $dados, true);
    }

    public function atualizarVaga(int|string $id, array $dados): array
    {
        return $this->client->patch("/company/jobs/{$id}", $dados, true);
    }

    public function alterarStatusVaga(int|string $id, string $status): array
    {
        return $this->client->patch("/company/jobs/{$id}/status", ['status' => $status], true);
    }

    public function candidatos(int|string $vagaId, array $filtros = []): array
    {
        return $this->client->get(
            "/company/jobs/{$vagaId}/applications",
            array_merge(['page' => 1, 'limit' => 50], $filtros),
            true
        );
    }

    public function alterarStatusCandidatura(int|string $vagaId, int|string $candidaturaId, string $status): array
    {
        return $this->client->patch(
            "/company/jobs/{$vagaId}/applications/{$candidaturaId}/status",
            ['status' => $status],
            true
        );
    }

    // Baixa o currículo de um candidato (valida que a vaga é da empresa). Binário.
    public function baixarCurriculoCandidato(int|string $vagaId, int|string $candidaturaId): array
    {
        return $this->client->downloadRaw(
            "/company/jobs/{$vagaId}/applications/{$candidaturaId}/resume",
            true
        );
    }

    // ─── Membros (somente ADMIN da empresa) ─────────────────────────────────
    public function membros(): array
    {
        return $this->client->get('/company/members', [], true);
    }

    public function criarMembro(array $dados): array
    {
        return $this->client->post('/company/members', $dados, true);
    }

    public function atualizarMembro(int|string $membroId, array $dados): array
    {
        return $this->client->patch("/company/members/{$membroId}", $dados, true);
    }

    public function removerMembro(int|string $membroId): array
    {
        return $this->client->delete("/company/members/{$membroId}", true);
    }

    public function resetarTotpMembro(int|string $membroId): array
    {
        return $this->client->post("/company/members/{$membroId}/totp/reset", [], true);
    }
}
