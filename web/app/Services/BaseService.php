<?php

namespace App\Services;

use App\Api\ApiClient;
use App\Auth\JwtManager;

abstract class BaseService
{
    protected ApiClient $client;
    protected JwtManager $jwt;

    public function __construct(ApiClient $client, JwtManager $jwt)
    {
        $this->client = $client;
        $this->jwt    = $jwt;
    }
}
