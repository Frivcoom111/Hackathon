<?php

namespace App\Services;

class CandidaturaService extends BaseService
{
    public function minhas(int $pagina = 1, int $limite = 20): array
    {
        return $this->client->get('/student/applications', [
            'page'  => $pagina,
            'limit' => $limite,
        ], true);
    }

    public function cancelar(int|string $id): array
    {
        return $this->client->delete("/student/applications/{$id}", true);
    }
}
