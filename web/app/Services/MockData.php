<?php

declare(strict_types=1);

namespace App\Services;

final class MockData
{
    /** @return array<int, array<string, mixed>> */
    public static function companies(): array
    {
        return [
            [
                'id' => 'company-tech-local',
                'name' => 'Tech Local',
                'cnpj' => '00.000.000/0001-00',
                'description' => 'Empresa regional de software e suporte.',
                'phone' => '(44) 3000-1000',
                'status' => 'APPROVED',
                'city' => 'Umuarama',
            ],
            [
                'id' => 'company-agencia-alfa',
                'name' => 'Agencia Alfa',
                'cnpj' => '11.111.111/0001-11',
                'description' => 'Marketing digital, conteudo e performance.',
                'phone' => '(44) 3000-2000',
                'status' => 'APPROVED',
                'city' => 'Umuarama',
            ],
            [
                'id' => 'company-winfo',
                'name' => 'Winfo',
                'cnpj' => '22.222.222/0001-22',
                'description' => 'Infraestrutura, redes e suporte corporativo.',
                'phone' => '(44) 3000-3000',
                'status' => 'APPROVED',
                'city' => 'Umuarama',
            ],
        ];
    }

    /** @return array<int, array<string, mixed>> */
    public static function jobs(): array
    {
        return [
            [
                'id' => 'job-backend-jr',
                'companyId' => 'company-tech-local',
                'company' => ['id' => 'company-tech-local', 'name' => 'Tech Local'],
                'title' => 'Estagio Backend Jr',
                'description' => 'Apoio no desenvolvimento de APIs, manutencao de sistemas internos e testes de funcionalidades.',
                'area' => 'Tecnologia',
                'requirements' => ['PHP ou Node.js basico', 'Git', 'Logica de programacao', 'Boa comunicacao'],
                'salary' => 1200,
                'location' => 'Umuarama, PR',
                'modality' => 'HYBRID',
                'status' => 'ACTIVE',
            ],
            [
                'id' => 'job-marketing-digital',
                'companyId' => 'company-agencia-alfa',
                'company' => ['id' => 'company-agencia-alfa', 'name' => 'Agencia Alfa'],
                'title' => 'Marketing Digital',
                'description' => 'Criacao de conteudos, acompanhamento de campanhas e apoio em redes sociais.',
                'area' => 'Marketing',
                'requirements' => ['Canva', 'Escrita clara', 'Organizacao', 'Nocoes de redes sociais'],
                'salary' => 900,
                'location' => 'Umuarama, PR',
                'modality' => 'PRESENCIAL',
                'status' => 'ACTIVE',
            ],
            [
                'id' => 'job-suporte-ti',
                'companyId' => 'company-winfo',
                'company' => ['id' => 'company-winfo', 'name' => 'Winfo'],
                'title' => 'Suporte TI',
                'description' => 'Atendimento a usuarios, triagem de chamados e documentacao de solucoes.',
                'area' => 'Tecnologia',
                'requirements' => ['Windows', 'Redes basicas', 'Atendimento ao usuario'],
                'salary' => 1100,
                'location' => 'Umuarama, PR',
                'modality' => 'REMOTE',
                'status' => 'ACTIVE',
            ],
            [
                'id' => 'job-dados-jr',
                'companyId' => 'company-tech-local',
                'company' => ['id' => 'company-tech-local', 'name' => 'Tech Local'],
                'title' => 'Analista de Dados Jr',
                'description' => 'Apoio em relatorios, consultas SQL e organizacao de indicadores.',
                'area' => 'Dados',
                'requirements' => ['Excel', 'SQL basico', 'Raciocinio analitico'],
                'salary' => 1400,
                'location' => 'Umuarama, PR',
                'modality' => 'HYBRID',
                'status' => 'ACTIVE',
            ],
        ];
    }

    /** @return array<int, array<string, mixed>> */
    public static function applications(): array
    {
        return [
            [
                'id' => 'app-001',
                'studentId' => 'student-demo',
                'studentName' => 'Ana Souza',
                'jobId' => 'job-backend-jr',
                'jobTitle' => 'Estagio Backend Jr',
                'companyName' => 'Tech Local',
                'status' => 'ANALYSING',
                'coverLetter' => 'Tenho interesse em backend e APIs.',
                'createdAt' => '2026-06-10',
            ],
            [
                'id' => 'app-002',
                'studentId' => 'student-demo',
                'studentName' => 'Ana Souza',
                'jobId' => 'job-marketing-digital',
                'jobTitle' => 'Marketing Digital',
                'companyName' => 'Agencia Alfa',
                'status' => 'PENDING',
                'coverLetter' => 'Quero desenvolver experiencia em campanhas.',
                'createdAt' => '2026-06-11',
            ],
            [
                'id' => 'app-003',
                'studentId' => 'student-marina',
                'studentName' => 'Marina Alves',
                'jobId' => 'job-backend-jr',
                'jobTitle' => 'Estagio Backend Jr',
                'companyName' => 'Tech Local',
                'status' => 'APPROVED',
                'coverLetter' => 'Ja fiz projetos academicos em PHP.',
                'createdAt' => '2026-06-12',
            ],
        ];
    }

    /** @return array<int, array<string, mixed>> */
    public static function notifications(): array
    {
        return [
            [
                'id' => 'notification-001',
                'title' => 'Candidatura em analise',
                'message' => 'Sua candidatura para Estagio Backend Jr entrou em analise.',
                'type' => 'APPLICATION_STATUS',
                'isRead' => false,
                'createdAt' => '2026-06-12',
            ],
            [
                'id' => 'notification-002',
                'title' => 'Nova vaga compativel',
                'message' => 'Encontramos uma vaga de Dados Jr compativel com seu curso.',
                'type' => 'JOB_RECOMMENDATION',
                'isRead' => true,
                'createdAt' => '2026-06-11',
            ],
        ];
    }
}
