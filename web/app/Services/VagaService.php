<?php

namespace App\Services;

class VagaService extends BaseService
{
    public function listar(array $filtros = []): array
    {
        // A rota GET /jobs exige JWT; envia o token (3o argumento).
        return $this->client->get('/jobs', $filtros, true);
    }

    public function buscar(int|string $id): array
    {
        return $this->client->get("/jobs/{$id}", [], true);
    }

    public function candidatar(int|string $vagaId, array $arquivo = []): array
    {
        if (!empty($arquivo)) {
            return $this->client->postMultipart("/jobs/{$vagaId}/apply", [], $arquivo, true);
        }
        return $this->client->postMultipart("/jobs/{$vagaId}/apply", [], [], true);
    }
}
