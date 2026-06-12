<?php

declare(strict_types=1);

namespace App\Models;

final class Student extends User
{
    public function __construct(
        string $id,
        string $email,
        string $role,
        bool $isActive,
        private readonly string $name,
        private readonly string $ra,
        private readonly string $cpf,
        private readonly ?string $phone,
        private readonly int $period,
        private readonly bool $isEligible,
        private readonly ?string $resumePath = null
    ) {
        parent::__construct($id, $email, $role, $isActive);
    }

    public static function fromArray(array $data): static
    {
        $user = is_array($data['user'] ?? null) ? $data['user'] : $data;

        return new static(
            self::string($user, 'id', self::string($data, 'userId', self::string($data, 'id'))),
            self::string($user, 'email', self::string($data, 'email')),
            self::string($user, 'role', 'STUDENT'),
            self::bool($user, 'isActive', true),
            self::string($data, 'name', self::string($data, 'nome')),
            self::string($data, 'ra'),
            self::string($data, 'cpf'),
            self::string($data, 'phone') ?: null,
            self::int($data, 'period', 1),
            self::bool($data, 'isEligible', true),
            self::string($data, 'resumePath') ?: null
        );
    }

    public function name(): string
    {
        return $this->name;
    }

    public function ra(): string
    {
        return $this->ra;
    }

    public function cpf(): string
    {
        return $this->cpf;
    }

    public function phone(): ?string
    {
        return $this->phone;
    }

    public function period(): int
    {
        return $this->period;
    }

    public function isEligible(): bool
    {
        return $this->isEligible;
    }

    public function resumePath(): ?string
    {
        return $this->resumePath;
    }
}
