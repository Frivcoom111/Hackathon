<?php

declare(strict_types=1);

namespace App\Http;

final class ApiClient
{
    public function __construct(
        private readonly string $baseUrl,
        private readonly int $timeout = 8
    ) {
    }

    /** @param array<string, mixed> $query */
    public function get(string $endpoint, array $query = [], ?string $token = null): array
    {
        $endpoint = $query === []
            ? $endpoint
            : $endpoint . '?' . http_build_query(array_filter($query, static fn (mixed $value): bool => $value !== '' && $value !== null));

        return $this->request('GET', $endpoint, null, $token);
    }

    /** @param array<string, mixed> $payload */
    public function post(string $endpoint, array $payload = [], ?string $token = null): array
    {
        return $this->request('POST', $endpoint, $payload, $token);
    }

    /** @param array<string, mixed> $payload */
    public function put(string $endpoint, array $payload = [], ?string $token = null): array
    {
        return $this->request('PUT', $endpoint, $payload, $token);
    }

    /** @param array<string, mixed> $payload */
    public function patch(string $endpoint, array $payload = [], ?string $token = null): array
    {
        return $this->request('PATCH', $endpoint, $payload, $token);
    }

    public function delete(string $endpoint, ?string $token = null): array
    {
        return $this->request('DELETE', $endpoint, null, $token);
    }

    /** @param array<string, mixed>|null $payload */
    private function request(string $method, string $endpoint, ?array $payload, ?string $token): array
    {
        if (!extension_loaded('curl')) {
            throw new ApiException('A extensao cURL do PHP nao esta habilitada.');
        }

        $url = $this->baseUrl . '/' . ltrim($endpoint, '/');
        $curl = curl_init($url);
        $headers = ['Accept: application/json'];

        curl_setopt_array($curl, [
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_CUSTOMREQUEST => $method,
            CURLOPT_TIMEOUT => $this->timeout,
        ]);

        if ($payload !== null) {
            $headers[] = 'Content-Type: application/json';
            curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($payload, JSON_THROW_ON_ERROR));
        }

        if ($token !== null && $token !== '') {
            $headers[] = 'Authorization: Bearer ' . $token;
        }

        curl_setopt($curl, CURLOPT_HTTPHEADER, $headers);

        $body = curl_exec($curl);
        $statusCode = (int) curl_getinfo($curl, CURLINFO_RESPONSE_CODE);

        if ($body === false) {
            $message = curl_error($curl) ?: 'Falha ao chamar a API.';
            curl_close($curl);
            throw new ApiException($message, $statusCode);
        }

        curl_close($curl);

        $decoded = $body !== '' ? json_decode($body, true) : [];

        if (!is_array($decoded)) {
            throw new ApiException('A API retornou JSON invalido.', $statusCode, $body);
        }

        if ($statusCode >= 400) {
            $message = (string) ($decoded['message'] ?? $decoded['error'] ?? 'Erro retornado pela API.');
            throw new ApiException($message, $statusCode, $body);
        }

        if (isset($decoded['data']) && is_array($decoded['data'])) {
            return $decoded['data'];
        }

        return $decoded;
    }
}
