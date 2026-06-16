<?php

namespace App\Api;

use App\Auth\JwtManager;
use App\Config\Config;
use GuzzleHttp\Client;
use GuzzleHttp\Exception\RequestException;

class ApiClient
{
    private Client $http;
    private JwtManager $jwt;

    public function __construct(JwtManager $jwt)
    {
        $this->jwt = $jwt;
        $this->http = new Client([
            'base_uri' => Config::API_URL,
            'timeout'  => Config::TIMEOUT,
            'http_errors' => false,
        ]);
    }

    public function get(string $path, array $query = [], bool $auth = false): array
    {
        try {
            $options = ['query' => $query];
            if ($auth) {
                $options['headers'] = $this->authHeader();
            }
            $response = $this->http->get($path, $options);
            return $this->parse($response);
        } catch (RequestException $e) {
            return $this->error($e->getMessage());
        }
    }

    public function post(string $path, array $body = [], bool $auth = false): array
    {
        try {
            $options = ['json' => $body];
            if ($auth) {
                $options['headers'] = $this->authHeader();
            }
            $response = $this->http->post($path, $options);
            return $this->parse($response);
        } catch (RequestException $e) {
            return $this->error($e->getMessage());
        }
    }

    public function patch(string $path, array $body = [], bool $auth = false): array
    {
        try {
            $options = ['json' => $body];
            if ($auth) {
                $options['headers'] = $this->authHeader();
            }
            $response = $this->http->patch($path, $options);
            return $this->parse($response);
        } catch (RequestException $e) {
            return $this->error($e->getMessage());
        }
    }

    public function delete(string $path, bool $auth = false): array
    {
        try {
            $options = [];
            if ($auth) {
                $options['headers'] = $this->authHeader();
            }
            $response = $this->http->delete($path, $options);
            return $this->parse($response);
        } catch (RequestException $e) {
            return $this->error($e->getMessage());
        }
    }

    public function postMultipart(string $path, array $fields = [], array $file = [], bool $auth = false): array
    {
        try {
            $multipart = [];
            foreach ($fields as $name => $value) {
                if ($value !== null && $value !== '') {
                    $multipart[] = ['name' => $name, 'contents' => (string) $value];
                }
            }
            if (!empty($file)) {
                $multipart[] = [
                    'name'     => $file['field'],
                    'contents' => fopen($file['path'], 'r'),
                    'filename' => $file['name'],
                    'headers'  => ['Content-Type' => $file['mime']],
                ];
            }

            $options = ['multipart' => $multipart];
            if ($auth) {
                $options['headers'] = $this->authHeader();
            }
            $response = $this->http->post($path, $options);
            return $this->parse($response);
        } catch (RequestException $e) {
            return $this->error($e->getMessage());
        }
    }

    public function patchMultipart(string $path, array $fields = [], array $file = [], bool $auth = false): array
    {
        try {
            $multipart = [];
            foreach ($fields as $name => $value) {
                if ($value !== null && $value !== '') {
                    $multipart[] = ['name' => $name, 'contents' => (string) $value];
                }
            }
            if (!empty($file)) {
                $multipart[] = [
                    'name'     => $file['field'],
                    'contents' => fopen($file['path'], 'r'),
                    'filename' => $file['name'],
                    'headers'  => ['Content-Type' => $file['mime']],
                ];
            }

            $options = ['multipart' => $multipart];
            if ($auth) {
                $options['headers'] = $this->authHeader();
            }
            $response = $this->http->patch($path, $options);
            return $this->parse($response);
        } catch (RequestException $e) {
            return $this->error($e->getMessage());
        }
    }

    /**
     * GET de conteúdo binário (ex.: currículo). NÃO faz json_decode — devolve o
     * corpo cru e os cabeçalhos relevantes para repassar no proxy de download.
     * Retorno: ['ok'=>bool,'status'=>int,'body'=>string,'contentType'=>string,'filename'=>?string,'message'=>?string]
     */
    public function downloadRaw(string $path, bool $auth = true): array
    {
        try {
            $options = [];
            if ($auth) {
                $options['headers'] = $this->authHeader();
            }
            $response = $this->http->get($path, $options);
            $status   = $response->getStatusCode();
            $body     = (string) $response->getBody();

            if ($status < 200 || $status >= 300) {
                $json = json_decode($body, true);
                return [
                    'ok'      => false,
                    'status'  => $status,
                    'message' => is_array($json) ? ($json['message'] ?? 'Falha ao baixar o arquivo.') : 'Falha ao baixar o arquivo.',
                ];
            }

            $disposition = $response->getHeaderLine('Content-Disposition');
            $filename    = null;
            if ($disposition && preg_match('/filename="?([^"]+)"?/', $disposition, $m)) {
                $filename = $m[1];
            }

            return [
                'ok'          => true,
                'status'      => $status,
                'body'        => $body,
                'contentType' => $response->getHeaderLine('Content-Type') ?: 'application/octet-stream',
                'filename'    => $filename,
            ];
        } catch (RequestException $e) {
            return ['ok' => false, 'status' => 0, 'message' => $e->getMessage()];
        }
    }

    private function authHeader(): array
    {
        return ['Authorization' => 'Bearer ' . $this->jwt->get()];
    }

    private function parse($response): array
    {
        $body = (string) $response->getBody();
        $data = json_decode($body, true);
        if ($data === null) {
            $status = $response->getStatusCode();
            if ($status >= 200 && $status < 300) {
                return ['success' => true];
            }
            return ['success' => false, 'message' => 'Resposta inválida da API'];
        }
        return $data;
    }

    private function error(string $message): array
    {
        return ['success' => false, 'message' => $message];
    }
}
