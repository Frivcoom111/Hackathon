<?php

declare(strict_types=1);

namespace App\Services;

use App\Http\ApiException;

final class AuthService extends AbstractApiService
{
    /** @return array{token:string,user:array<string,mixed>,role:string} */
    public function login(string $email, string $password): array
    {
        try {
            $response = $this->api->post('/auth/login', [
                'email' => $email,
                'password' => $password,
            ]);

            $token = (string) ($response['token'] ?? '');

            if ($token === '') {
                throw new ApiException('A API nao retornou um token de acesso.', 502);
            }

            return $this->sessionFromToken($token);
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            $role = str_contains(strtolower($email), 'empresa') ? 'COMPANY' : 'STUDENT';
            $name = $role === 'COMPANY' ? 'Empresa Demo' : 'Aluno Demo';

            return [
                'token' => 'mock-' . strtolower($role) . '-token',
                'user' => [
                    'id' => $role === 'COMPANY' ? 'company-member-demo' : 'student-demo',
                    'email' => $email,
                    'name' => $name,
                    'role' => $role,
                    'isActive' => true,
                ],
                'role' => $role,
            ];
        }
    }

    /** @param array<string, mixed> $payload */
    public function registerStudent(array $payload): array
    {
        try {
            return $this->api->post('/auth/register/student', $this->studentPayload($payload));
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            return ['message' => 'Aluno cadastrado em modo demonstracao.'];
        }
    }

    /** @param array<string, mixed> $payload */
    public function registerCompany(array $payload): array
    {
        try {
            return $this->api->post('/auth/register/company', $this->companyPayload($payload));
        } catch (ApiException $exception) {
            if (!$this->mockEnabled()) {
                throw $exception;
            }

            return ['message' => 'Empresa enviada para analise em modo demonstracao.'];
        }
    }



    /** @return array{token:string,user:array<string,mixed>,role:string} */
    private function sessionFromToken(string $token): array
    {
        $profile = $this->api->get('/auth/me', [], $token);
        $userNode = is_array($profile['user'] ?? null) ? $profile['user'] : [];
        $company = is_array($profile['company'] ?? null) ? $profile['company'] : [];
        $role = (string) ($userNode['role'] ?? $profile['role'] ?? ($company !== [] ? 'COMPANY' : 'STUDENT'));

        return [
            'token' => $token,
            'user' => [
                'id' => (string) ($userNode['id'] ?? $profile['userId'] ?? $profile['id'] ?? ''),
                'email' => (string) ($userNode['email'] ?? ''),
                'name' => (string) ($profile['name'] ?? $company['name'] ?? $userNode['email'] ?? ''),
                'role' => $role,
                'isActive' => (bool) ($userNode['isActive'] ?? true),
                'profile' => $profile,
            ],
            'role' => $role,
        ];
    }

    /** @param array<string, mixed> $payload @return array<string, mixed> */
    private function studentPayload(array $payload): array
    {
        $courseId = (string) ($payload['courseId'] ?? app_config('default_course_id', ''));

        $data = [
            'email' => (string) ($payload['email'] ?? ''),
            'password' => (string) ($payload['password'] ?? ''),
            'name' => (string) ($payload['name'] ?? ''),
            'ra' => (string) ($payload['ra'] ?? ''),
            'cpf' => $this->digits((string) ($payload['cpf'] ?? '')),
            'phone' => $this->nullableDigits((string) ($payload['phone'] ?? '')),
            'courseId' => $courseId,
            'status' => (string) ($payload['status'] ?? 'ACTIVE'),
            'startedAt' => (string) ($payload['startedAt'] ?? date('Y-m-d')),
        ];

        return array_filter($data, static fn (mixed $value): bool => $value !== null && $value !== '');
    }

    /** @param array<string, mixed> $payload @return array<string, mixed> */
    private function companyPayload(array $payload): array
    {
        $address = is_array($payload['address'] ?? null) ? $payload['address'] : [];
        $member = is_array($payload['member'] ?? null) ? $payload['member'] : [];

        $data = [
            'email' => (string) ($payload['email'] ?? ''),
            'password' => (string) ($payload['password'] ?? ''),
            'name' => (string) ($payload['name'] ?? ''),
            'cnpj' => $this->digits((string) ($payload['cnpj'] ?? '')),
            'description' => (string) ($payload['description'] ?? ''),
            'phone' => $this->digits((string) ($payload['phone'] ?? '')),
            'address' => [
                'street' => (string) ($address['street'] ?? ''),
                'number' => (string) ($address['number'] ?? ''),
                'complement' => (string) ($address['complement'] ?? ''),
                'district' => (string) ($address['district'] ?? ''),
                'city' => (string) ($address['city'] ?? ''),
                'state' => strtoupper((string) ($address['state'] ?? '')),
                'zipCode' => $this->digits((string) ($address['zipCode'] ?? '')),
            ],
            'member' => [
                'name' => (string) ($member['name'] ?? $payload['responsibleName'] ?? ''),
                'cpf' => $this->digits((string) ($member['cpf'] ?? '')),
                'phone' => $this->nullableDigits((string) ($member['phone'] ?? '')),
            ],
        ];

        $data['address'] = array_filter($data['address'], static fn (mixed $value): bool => $value !== null && $value !== '');
        $data['member'] = array_filter($data['member'], static fn (mixed $value): bool => $value !== null && $value !== '');

        return $data;
    }

    private function digits(string $value): string
    {
        return preg_replace('/\D+/', '', $value) ?? '';
    }

    private function nullableDigits(string $value): ?string
    {
        $digits = $this->digits($value);
        return $digits === '' ? null : $digits;
    }
}


