<?php

declare(strict_types=1);

use App\Core\Session;

function app_config(?string $key = null, mixed $default = null): mixed
{
    $config = $GLOBALS['config'] ?? [];

    if ($key === null) {
        return $config;
    }

    return $config[$key] ?? $default;
}

function e(mixed $value): string
{
    return htmlspecialchars((string) $value, ENT_QUOTES, 'UTF-8');
}

function url(string $path = ''): string
{
    $base = (string) app_config('url', '');

    if ($base === '') {
        $scheme = (!empty($_SERVER['HTTPS']) && $_SERVER['HTTPS'] !== 'off') ? 'https' : 'http';
        $host = $_SERVER['HTTP_HOST'] ?? '127.0.0.1:8080';
        $base = $scheme . '://' . $host;
    }

    return $base . '/' . ltrim($path, '/');
}

function asset(string $path): string
{
    return url('/assets/' . ltrim($path, '/'));
}

function redirect(string $path): never
{
    header('Location: ' . url($path));
    exit;
}

function selected(mixed $actual, mixed $expected): string
{
    return (string) $actual === (string) $expected ? 'selected' : '';
}

function checked(bool $value): string
{
    return $value ? 'checked' : '';
}

function money(mixed $value): string
{
    if ($value === null || $value === '') {
        return 'Bolsa a combinar';
    }

    return 'R$ ' . number_format((float) $value, 2, ',', '.');
}

function modality_label(string $modality): string
{
    return [
        'PRESENCIAL' => 'Presencial',
        'REMOTE' => 'Remoto',
        'HYBRID' => 'Hibrido',
    ][$modality] ?? $modality;
}

function status_label(string $status): string
{
    return [
        'PENDING' => 'Pendente',
        'ANALYSING' => 'Em analise',
        'APPROVED' => 'Aprovado',
        'REJECTED' => 'Reprovado',
        'CANCELLED' => 'Cancelado',
        'ACTIVE' => 'Ativa',
        'PAUSED' => 'Pausada',
        'CLOSED' => 'Encerrada',
        'BLOCKED' => 'Bloqueada',
    ][$status] ?? $status;
}

function status_class(string $status): string
{
    return [
        'PENDING' => 'tag-warning',
        'ANALYSING' => 'tag-info',
        'APPROVED' => 'tag-success',
        'REJECTED' => 'tag-muted',
        'CANCELLED' => 'tag-muted',
        'ACTIVE' => 'tag-success',
        'PAUSED' => 'tag-warning',
        'CLOSED' => 'tag-muted',
        'BLOCKED' => 'tag-danger',
    ][$status] ?? 'tag-muted';
}

function is_active(string $path): string
{
    $current = parse_url($_SERVER['REQUEST_URI'] ?? '/', PHP_URL_PATH) ?: '/';
    return $current === $path ? 'active' : '';
}

function csrf_field(): string
{
    return '<input type="hidden" name="_token" value="' . e(Session::csrfToken()) . '">';
}
