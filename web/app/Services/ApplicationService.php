<?php

declare(strict_types=1);

namespace App\Services;

use App\Http\ApiException;
use App\Models\Application;

final class ApplicationService extends AbstractApiService
{
    public function apply(string $jobId, ?string $coverLetter = null): array
    {
        try {
            return $this->api->post('/jobs/' . $jobId . '/apply', [
                'coverLetter' => $coverLetter,
            ], $this->token());
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            return ['message' => 'Candidatura enviada em modo demonstracao.'];
        }
    }

    /** @return array<int, Application> */
    public function mine(): array
    {
        try {
            $response = $this->api->get('/student/applications', [], $this->token());
            $items = $response['items'] ?? $response;

            return Application::collection(is_array($items) ? $items : []);
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            return Application::collection(array_slice(MockData::applications(), 0, 2));
        }
    }

    /** @return array<int, Application> */
    public function byJob(string $jobId): array
    {
        try {
            $response = $this->api->get('/company/jobs/' . $jobId . '/applications', [], $this->token());
            $items = $response['items'] ?? $response;
            $items = is_array($items) ? $this->withJobId($items, $jobId) : [];

            return Application::collection($items);
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            $items = array_filter(MockData::applications(), static fn (array $application): bool => (string) $application['jobId'] === $jobId);
            return Application::collection(array_values($items));
        }
    }

    public function updateStatus(string $applicationId, string $status, ?string $jobId = null): array
    {
        try {
            if ($jobId === null || $jobId === '') {
                throw new ApiException('A vaga da candidatura nao foi informada.', 422);
            }

            return $this->api->patch('/company/jobs/' . $jobId . '/applications/' . $applicationId . '/status', ['status' => $status], $this->token());
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            return ['message' => 'Status atualizado em modo demonstracao.'];
        }
    }

    public function cancel(string $applicationId): array
    {
        try {
            return $this->api->delete('/student/applications/' . $applicationId, $this->token());
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            return ['message' => 'Candidatura cancelada em modo demonstracao.'];
        }
    }

    /** @param array<int, mixed> $items @return array<int, mixed> */
    private function withJobId(array $items, string $jobId): array
    {
        return array_map(static function (mixed $item) use ($jobId): mixed {
            if (is_array($item) && !isset($item['jobId'])) {
                $item['jobId'] = $jobId;
            }

            return $item;
        }, $items);
    }
}
