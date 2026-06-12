<?php

declare(strict_types=1);

namespace App\Services;

use App\Http\ApiException;
use App\Models\Job;

final class JobService extends AbstractApiService
{
    /** @param array<string, mixed> $filters */
    public function list(array $filters = []): array
    {
        try {
            $response = $this->api->get('/jobs', $this->publicFilters($filters));
            $items = $response['items'] ?? $response;

            return Job::collection(is_array($items) ? $items : []);
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            return Job::collection($this->filterMockJobs($filters));
        }
    }

    public function find(string $id): ?Job
    {
        try {
            return Job::fromArray($this->api->get('/jobs/' . $id));
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            foreach (MockData::jobs() as $job) {
                if ((string) $job['id'] === $id) {
                    return Job::fromArray($job);
                }
            }

            return null;
        }
    }

    /** @param array<string, mixed> $filters @return array<int, Job> */
    public function listCompany(array $filters = []): array
    {
        try {
            $response = $this->api->get('/company/jobs', $this->paginationFilters($filters), $this->token());
            $items = $response['items'] ?? $response;

            return Job::collection(is_array($items) ? $this->filterCompanyItems($items, $filters) : []);
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            return Job::collection($this->filterMockJobs($filters));
        }
    }

    public function findCompany(string $id): ?Job
    {
        try {
            return Job::fromArray($this->api->get('/company/jobs/' . $id, [], $this->token()));
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            foreach (MockData::jobs() as $job) {
                if ((string) $job['id'] === $id) {
                    return Job::fromArray($job);
                }
            }

            return null;
        }
    }

    /** @param array<string, mixed> $payload */
    public function create(array $payload): array
    {
        try {
            unset($payload['status']);
            return $this->api->post('/company/jobs', $this->normalizePayload($payload), $this->token());
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            return ['message' => 'Vaga criada em modo demonstracao.'];
        }
    }

    /** @param array<string, mixed> $payload */
    public function update(string $id, array $payload): array
    {
        try {
            $status = (string) ($payload['status'] ?? '');
            unset($payload['status']);

            $response = $this->api->patch('/company/jobs/' . $id, $this->normalizePayload($payload), $this->token());

            if ($status !== '') {
                $response = $this->changeStatus($id, $status);
            }

            return $response;
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            return ['message' => 'Vaga atualizada em modo demonstracao.'];
        }
    }

    public function delete(string $id): array
    {
        return $this->changeStatus($id, 'CLOSED');
    }

    public function changeStatus(string $id, string $status): array
    {
        try {
            return $this->api->patch('/company/jobs/' . $id . '/status', ['status' => $status], $this->token());
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            return ['message' => 'Status da vaga atualizado em modo demonstracao.'];
        }
    }

    /** @param array<string, mixed> $filters @return array<int, array<string, mixed>> */
    private function filterMockJobs(array $filters): array
    {
        $jobs = MockData::jobs();

        return array_values(array_filter($jobs, static function (array $job) use ($filters): bool {
            foreach (['search', 'area', 'modality', 'location', 'status'] as $filter) {
                $value = trim((string) ($filters[$filter] ?? ''));

                if ($value === '') {
                    continue;
                }

                $haystack = strtolower((string) ($job[$filter] ?? $job['title'] ?? '') . ' ' . ($job['company']['name'] ?? ''));

                if ($filter === 'search') {
                    if (!str_contains($haystack, strtolower($value))) {
                        return false;
                    }
                    continue;
                }

                if (!str_contains(strtolower((string) ($job[$filter] ?? '')), strtolower($value))) {
                    return false;
                }
            }

            return true;
        }));
    }

    /** @param array<string, mixed> $payload @return array<string, mixed> */
    private function normalizePayload(array $payload): array
    {
        if (isset($payload['requirements']) && is_string($payload['requirements'])) {
            $payload['requirements'] = trim($payload['requirements']);
        }

        if (($payload['salary'] ?? '') !== '') {
            $payload['salary'] = (float) $payload['salary'];
        } else {
            unset($payload['salary']);
        }

        if (($payload['courseId'] ?? '') === '') {
            unset($payload['courseId']);
        }

        return array_filter($payload, static fn (mixed $value): bool => $value !== null && $value !== '');
    }

    /** @param array<string, mixed> $filters @return array<string, mixed> */
    private function publicFilters(array $filters): array
    {
        return array_intersect_key($filters, array_flip(['page', 'limit', 'courseId', 'area', 'modality', 'search']));
    }

    /** @param array<string, mixed> $filters @return array<string, mixed> */
    private function paginationFilters(array $filters): array
    {
        return array_intersect_key($filters, array_flip(['page', 'limit']));
    }

    /** @param array<int, mixed> $items @param array<string, mixed> $filters @return array<int, mixed> */
    private function filterCompanyItems(array $items, array $filters): array
    {
        $status = (string) ($filters['status'] ?? '');

        if ($status === '') {
            return $items;
        }

        return array_values(array_filter(
            $items,
            static fn (mixed $job): bool => is_array($job) && (string) ($job['status'] ?? '') === $status
        ));
    }
}
