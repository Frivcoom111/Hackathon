<?php

declare(strict_types=1);

namespace App\Models;

final class Job extends BaseModel
{
    /** @param array<int, string> $requirements */
    public function __construct(
        private readonly string $id,
        private readonly string $companyId,
        private readonly ?string $courseId,
        private readonly string $companyName,
        private readonly string $title,
        private readonly string $description,
        private readonly string $area,
        private readonly array $requirements,
        private readonly ?float $salary,
        private readonly string $location,
        private readonly string $modality,
        private readonly string $status
    ) {
    }

    public static function fromArray(array $data): static
    {
        $company = is_array($data['company'] ?? null) ? $data['company'] : [];
        $requirements = $data['requirements'] ?? [];

        if (is_string($requirements)) {
            $requirements = array_filter(array_map('trim', preg_split('/\r\n|\r|\n/', $requirements) ?: []));
        }

        return new static(
            self::string($data, 'id'),
            self::string($data, 'companyId', self::string($company, 'id')),
            self::string($data, 'courseId') ?: null,
            self::string($company, 'name', self::string($data, 'companyName', 'Empresa')),
            self::string($data, 'title', self::string($data, 'titulo')),
            self::string($data, 'description', self::string($data, 'descricao')),
            self::string($data, 'area', 'Tecnologia'),
            is_array($requirements) ? array_values($requirements) : [],
            isset($data['salary']) ? (float) $data['salary'] : (isset($data['scholarship']) ? (float) $data['scholarship'] : null),
            self::string($data, 'location', self::string($data, 'city', 'Umuarama, PR')),
            self::string($data, 'modality', 'PRESENCIAL'),
            self::string($data, 'status', 'ACTIVE')
        );
    }

    public function id(): string
    {
        return $this->id;
    }

    public function companyId(): string
    {
        return $this->companyId;
    }

    public function courseId(): ?string
    {
        return $this->courseId;
    }

    public function companyName(): string
    {
        return $this->companyName;
    }

    public function title(): string
    {
        return $this->title;
    }

    public function description(): string
    {
        return $this->description;
    }

    public function area(): string
    {
        return $this->area;
    }

    /** @return array<int, string> */
    public function requirements(): array
    {
        return $this->requirements;
    }

    public function salary(): ?float
    {
        return $this->salary;
    }

    public function location(): string
    {
        return $this->location;
    }

    public function modality(): string
    {
        return $this->modality;
    }

    public function status(): string
    {
        return $this->status;
    }
}
