<?php

declare(strict_types=1);

namespace App\Core;

final class Session
{
    public static function start(): void
    {
        if (session_status() !== PHP_SESSION_ACTIVE) {
            session_start();
        }
    }

    public static function set(string $key, mixed $value): void
    {
        $_SESSION[$key] = $value;
    }

    public static function get(string $key, mixed $default = null): mixed
    {
        return $_SESSION[$key] ?? $default;
    }

    public static function forget(string $key): void
    {
        unset($_SESSION[$key]);
    }

    public static function flash(string $key, string $message): void
    {
        $_SESSION['_flash'][$key] = $message;
    }

    public static function pullFlash(string $key): ?string
    {
        $message = $_SESSION['_flash'][$key] ?? null;
        unset($_SESSION['_flash'][$key]);

        return $message;
    }

    public static function csrfToken(): string
    {
        if (!isset($_SESSION['_csrf_token'])) {
            $_SESSION['_csrf_token'] = bin2hex(random_bytes(20));
        }

        return (string) $_SESSION['_csrf_token'];
    }

    public static function login(string $token, array $user, string $role): void
    {
        self::set('token', $token);
        self::set('user', $user);
        self::set('role', $role);
    }

    public static function isAuthenticated(): bool
    {
        return self::get('token') !== null;
    }

    public static function token(): ?string
    {
        $token = self::get('token');
        return is_string($token) ? $token : null;
    }

    /** @return array<string, mixed> */
    public static function user(): array
    {
        $user = self::get('user', []);
        return is_array($user) ? $user : [];
    }

    public static function role(): ?string
    {
        $role = self::get('role');
        return is_string($role) ? $role : null;
    }

    public static function logout(): void
    {
        self::forget('token');
        self::forget('user');
        self::forget('role');
    }
}
