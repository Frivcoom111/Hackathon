<?php

declare(strict_types=1);

namespace App\Models;

final class Notification extends BaseModel
{
    public function __construct(
        private readonly string $id,
        private readonly string $title,
        private readonly string $message,
        private readonly string $type,
        private readonly bool $isRead,
        private readonly string $createdAt
    ) {
    }

    public static function fromArray(array $data): static
    {
        return new static(
            self::string($data, 'id'),
            self::string($data, 'title'),
            self::string($data, 'message'),
            self::string($data, 'type', 'INFO'),
            self::bool($data, 'isRead', self::bool($data, 'read', false)),
            self::string($data, 'createdAt', date('Y-m-d'))
        );
    }

    public function id(): string
    {
        return $this->id;
    }

    public function title(): string
    {
        return $this->title;
    }

    public function message(): string
    {
        return $this->message;
    }

    public function type(): string
    {
        return $this->type;
    }

    public function isRead(): bool
    {
        return $this->isRead;
    }

    public function createdAt(): string
    {
        return $this->createdAt;
    }
}
