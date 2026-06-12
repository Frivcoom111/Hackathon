<?php

declare(strict_types=1);

namespace App\Services;

use App\Core\Session;
use App\Http\ApiClient;

abstract class AbstractApiService
{
    protected ApiClient $api;

    public function __construct(?ApiClient $api = null)
    {
        $this->api = $api ?? new ApiClient((string) app_config('api_base_url'));
    }

    protected function token(): ?string
    {
        return Session::token();
    }

    protected function mockEnabled(): bool
    {
        return (bool) app_config('mock_enabled', true);
    }
}
