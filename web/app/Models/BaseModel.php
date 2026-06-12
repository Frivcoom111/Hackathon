<?php

declare(strict_types=1);

namespace App\Models;

abstract class BaseModel
{
    /** @param array<string, mixed> $data */
    abstract public static function fromArray(array $data): static;

    /** @param array<int, array<string, mixed>> $items */
    public static function collection(array $items): array
    {
        return array_map(static fn (array $item): static => static::fromArray($item), $items);
    }

    /** @param array<string, mixed> $data */
    protected static function string(array $data, string $key, string $default = ''): string
    {
        return (string) ($data[$key] ?? $default);
    }

    /** @param array<string, mixed> $data */
    protected static function int(array $data, string $key, int $default = 0): int
    {
        return (int) ($data[$key] ?? $default);
    }

    /** @param array<string, mixed> $data */
    protected static function float(array $data, string $key, float $default = 0): float
    {
        return (float) ($data[$key] ?? $default);
    }

    /** @param array<string, mixed> $data */
    protected static function bool(array $data, string $key, bool $default = false): bool
    {
        return (bool) ($data[$key] ?? $default);
    }
}
