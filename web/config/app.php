<?php

declare(strict_types=1);

$env = static function (string $key, mixed $default = null): mixed {
    $value = $_ENV[$key] ?? getenv($key);

    if ($value === false || $value === null || $value === '') {
        return $default;
    }

    return $value;
};

return [
    'name' => $env('APP_NAME', 'Portal de Estagios UniALFA'),
    'url' => rtrim((string) $env('APP_URL', ''), '/'),
    'api_base_url' => rtrim((string) $env('API_BASE_URL', 'http://127.0.0.1:3000'), '/'),
    'mock_enabled' => filter_var($env('USE_MOCK_DATA', 'true'), FILTER_VALIDATE_BOOLEAN),
    'default_course_id' => (string) $env('DEFAULT_COURSE_ID', ''),
];
