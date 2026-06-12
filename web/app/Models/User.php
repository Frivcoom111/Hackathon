<?php

declare(strict_types=1);

namespace App\Models;

class User extends BaseModel
{
    public function __construct(
        protected readonly string $id,
        protected readonly string $email,
        protected readonly string $role,
        protected readonly bool $isActive = true
    ) {
    }

    public static function fromArray(array $data): static
    {
        return new static(
            self::string($data, 'id'),
            self::string($data, 'email'),
            self::string($data, 'role', 'STUDENT'),
            self::bool($data, 'isActive', true)
        );
    }

    public function id(): string
    {
        return $this->id;
    }

    public function email(): string
    {
        return $this->email;
    }

    public function role(): string
    {
        return $this->role;
    }

    public function isActive(): bool
    {
        return $this->isActive;
    }
}
