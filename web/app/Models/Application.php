<?php

declare(strict_types=1);

namespace App\Models;

final class Application extends BaseModel
{
    public function __construct(
        private readonly string $id,
        private readonly string $studentId,
        private readonly string $studentName,
        private readonly string $jobId,
        private readonly string $jobTitle,
        private readonly string $companyName,
        private readonly string $status,
        private readonly ?string $coverLetter,
        private readonly string $createdAt
    ) {
    }

    public static function fromArray(array $data): static
    {
        $student = is_array($data['student'] ?? null) ? $data['student'] : [];
        $job = is_array($data['job'] ?? null) ? $data['job'] : [];
        $company = is_array($job['company'] ?? null) ? $job['company'] : [];

        return new static(
            self::string($data, 'id'),
            self::string($data, 'studentId', self::string($student, 'id')),
            self::string($student, 'name', self::string($data, 'studentName', 'Aluno')),
            self::string($data, 'jobId', self::string($job, 'id')),
            self::string($job, 'title', self::string($data, 'jobTitle', 'Vaga')),
            self::string($company, 'name', self::string($data, 'companyName', 'Empresa')),
            self::string($data, 'status', 'PENDING'),
            self::string($data, 'coverLetter') ?: null,
            self::string($data, 'createdAt', date('Y-m-d'))
        );
    }

    public function id(): string
    {
        return $this->id;
    }

    public function studentId(): string
    {
        return $this->studentId;
    }

    public function studentName(): string
    {
        return $this->studentName;
    }

    public function jobId(): string
    {
        return $this->jobId;
    }

    public function jobTitle(): string
    {
        return $this->jobTitle;
    }

    public function companyName(): string
    {
        return $this->companyName;
    }

    public function status(): string
    {
        return $this->status;
    }

    public function coverLetter(): ?string
    {
        return $this->coverLetter;
    }

    public function createdAt(): string
    {
        return $this->createdAt;
    }
}
