<?php

if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

require_once __DIR__ . '/vendor/autoload.php';

// Autoload manual das classes app/ (sem PSR-4 no composer para manter simples)
spl_autoload_register(function (string $class): void {
    $prefix = 'App\\';
    if (strncmp($class, $prefix, strlen($prefix)) !== 0) {
        return;
    }
    $relative = str_replace('\\', '/', substr($class, strlen($prefix)));
    $file = __DIR__ . '/app/' . $relative . '.php';
    if (file_exists($file)) {
        require_once $file;
    }
});

use App\Api\Api;
use App\Api\ApiClient;
use App\Auth\JwtManager;

$jwt = new JwtManager();
$client = new ApiClient($jwt);
$api = new Api($client, $jwt);

define('BASE', '');
define('API_URL', 'http://localhost:3000');
