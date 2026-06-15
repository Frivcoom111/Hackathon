<?php

namespace App\Services;

class CursoService extends BaseService
{
    public function listar(): array
    {
        $resp = $this->client->get('/courses');
        return $resp['data'] ?? $resp ?? [];
    }
}
