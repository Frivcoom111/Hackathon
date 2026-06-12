<?php

declare(strict_types=1);

namespace App\Models;

final class Company extends BaseModel
{
    public function __construct(
        private readonly string $id,
        private readonly string $name,
        private readonly string $cnpj,
        private readonly ?string $description,
        private readonly ?string $phone,
        private readonly string $status,
        private readonly ?string $city = null
    ) {
    }

    public static function fromArray(array $data): static
    {
        $address = is_array($data['address'] ?? null) ? $data['address'] : [];

        return new static(
            self::string($data, 'id'),
            self::string($data, 'name', self::string($data, 'razao_social')),
            self::string($data, 'cnpj'),
            self::string($data, 'description') ?: null,
            self::string($data, 'phone') ?: null,
            self::string($data, 'status', 'PENDING'),
            self::string($address, 'city', self::string($data, 'city')) ?: null
        );
    }

    public function id(): string
    {
        return $this->id;
    }

    public function name(): string
    {
        return $this->name;
    }

    public function cnpj(): string
    {
        return $this->cnpj;
    }

    public function description(): ?string
    {
        return $this->description;
    }

    public function phone(): ?string
    {
        return $this->phone;
    }

    public function status(): string
    {
        return $this->status;
    }

    public function city(): ?string
    {
        return $this->city;
    }
}
