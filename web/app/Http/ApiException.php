<?php

declare(strict_types=1);

namespace App\Http;

final class ApiException extends \RuntimeException
{
    public function __construct(
        string $message,
        private readonly int $statusCode = 0,
        private readonly ?string $body = null
    ) {
        parent::__construct($message, $statusCode);
    }

    public function statusCode(): int
    {
        return $this->statusCode;
    }

    public function body(): ?string
    {
        return $this->body;
    }
}
