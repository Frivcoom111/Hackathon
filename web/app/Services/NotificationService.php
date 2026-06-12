<?php

declare(strict_types=1);

namespace App\Services;

use App\Http\ApiException;
use App\Models\Notification;

final class NotificationService extends AbstractApiService
{
    /** @return array<int, Notification> */
    public function mine(): array
    {
        if (!$this->mockEnabled()) {
            return [];
        }

        return Notification::collection(MockData::notifications());
    }

    public function markAsRead(string $id): array
    {
        return ['message' => 'Notificacao marcada como lida em modo demonstracao.'];
    }
}
