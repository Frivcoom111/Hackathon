<?php

namespace App\Services;

class VagaService extends BaseService
{
    public function listar(array $filtros = []): array
    {
        return $this->client->get('/jobs', $filtros);
    }

    public function buscar(int|string $id): array
    {
        return $this->client->get("/jobs/{$id}");
    }

    public function candidatar(int|string $vagaId, array $arquivo = []): array
    {
        if (!empty($arquivo)) {
            return $this->client->postMultipart("/jobs/{$vagaId}/apply", [], $arquivo, true);
        }
        return $this->client->postMultipart("/jobs/{$vagaId}/apply", [], [], true);
    }
}
