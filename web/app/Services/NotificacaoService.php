<?php

namespace App\Services;

class NotificacaoService extends BaseService
{
    /**
     * Lista notificações do usuário autenticado.
     * $unread: true = só não lidas, false = só lidas, null = todas.
     */
    public function listar(?bool $unread = null, int $pagina = 1, int $limite = 20): array
    {
        $query = ['page' => $pagina, 'limit' => $limite];
        if ($unread !== null) {
            $query['unread'] = $unread ? 'true' : 'false';
        }
        return $this->client->get('/notifications', $query, true);
    }

    /**
     * Resumo para o sino do header: lista das últimas não lidas + total (badge).
     */
    public function naoLidas(int $limite = 8): array
    {
        return $this->client->get('/notifications', [
            'unread' => 'true',
            'page'   => 1,
            'limit'  => $limite,
        ], true);
    }

    public function marcarLida(string $id): array
    {
        return $this->client->patch("/notifications/{$id}/read", [], true);
    }

    public function marcarTodasLidas(): array
    {
        return $this->client->patch('/notifications/read-all', [], true);
    }
}
