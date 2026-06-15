<?php

function api_base_url(): string
{
    return rtrim(getenv('API_BASE_URL') ?: 'http://localhost:3000', '/');
}

function api_decode_response(string|false|null $response): ?array
{
    if (!is_string($response) || $response === '') {
        return null;
    }

    $json = json_decode($response, true);
    return is_array($json) ? $json : null;
}

function api_request(string $method, string $path, mixed $body = null, array $headers = []): ?array
{
    $url = api_base_url() . '/' . ltrim($path, '/');

    if (function_exists('curl_init')) {
        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_TIMEOUT, 5);

        if ($method !== 'GET') {
            curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
        }

        if ($body !== null) {
            if (api_has_file($body)) {
                curl_setopt($ch, CURLOPT_POSTFIELDS, api_prepare_curl_files($body));
            } else {
                $headers[] = 'Content-Type: application/json';
                curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($body));
            }
        }

        if ($headers !== []) {
            curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        }

        $response = curl_exec($ch);
        curl_close($ch);

        return api_decode_response($response);
    }

    $context = [
        'http' => [
            'method' => $method,
            'timeout' => 5,
            'ignore_errors' => true,
            'header' => implode("\r\n", $headers),
        ],
    ];

    if ($body !== null) {
        if (api_has_file($body)) {
            [$payload, $contentType] = api_build_multipart($body);
            $context['http']['content'] = $payload;
            $context['http']['header'] = trim(($context['http']['header'] ? $context['http']['header'] . "\r\n" : '') . $contentType);
        } else {
            $context['http']['content'] = json_encode($body);
            $context['http']['header'] = trim(($context['http']['header'] ? $context['http']['header'] . "\r\n" : '') . 'Content-Type: application/json');
        }
    }

    return api_decode_response(@file_get_contents($url, false, stream_context_create($context)));
}

function api_get(string $path, ?string $token = null): ?array
{
    $headers = $token ? ['Authorization: Bearer ' . $token] : [];
    return api_request('GET', $path, null, $headers);
}

function api_post_json(string $path, array $payload, ?string $token = null): ?array
{
    $headers = $token ? ['Authorization: Bearer ' . $token] : [];
    return api_request('POST', $path, $payload, $headers);
}

function api_patch_json(string $path, array $payload, ?string $token = null): ?array
{
    $headers = $token ? ['Authorization: Bearer ' . $token] : [];
    return api_request('PATCH', $path, $payload, $headers);
}

function api_post_form(string $path, array $payload, ?string $token = null): ?array
{
    $headers = $token ? ['Authorization: Bearer ' . $token] : [];
    return api_request('POST', $path, $payload, $headers);
}

function api_has_file(mixed $payload): bool
{
    if (!is_array($payload)) {
        return false;
    }

    foreach ($payload as $value) {
        if ((class_exists('CURLFile') && $value instanceof CURLFile) || (is_array($value) && isset($value['tmp_name'], $value['name']))) {
            return true;
        }
    }

    return false;
}

function api_prepare_curl_files(array $payload): array
{
    if (!class_exists('CURLFile')) {
        return $payload;
    }

    foreach ($payload as $key => $value) {
        if (is_array($value) && isset($value['tmp_name'], $value['name'])) {
            $payload[$key] = new CURLFile(
                $value['tmp_name'],
                $value['type'] ?? 'application/octet-stream',
                $value['name']
            );
        }
    }

    return $payload;
}

function api_build_multipart(array $payload): array
{
    $boundary = '----portal-estagio-' . bin2hex(random_bytes(8));
    $body = '';

    foreach ($payload as $name => $value) {
        $body .= "--{$boundary}\r\n";

        if (is_array($value) && isset($value['tmp_name'], $value['name'])) {
            $filename = basename((string)$value['name']);
            $type = $value['type'] ?? 'application/octet-stream';
            $content = is_readable($value['tmp_name']) ? file_get_contents($value['tmp_name']) : '';

            $body .= "Content-Disposition: form-data; name=\"{$name}\"; filename=\"{$filename}\"\r\n";
            $body .= "Content-Type: {$type}\r\n\r\n";
            $body .= $content . "\r\n";
        } else {
            $body .= "Content-Disposition: form-data; name=\"{$name}\"\r\n\r\n";
            $body .= (string)$value . "\r\n";
        }
    }

    $body .= "--{$boundary}--\r\n";

    return [$body, 'Content-Type: multipart/form-data; boundary=' . $boundary];
}

function api_items(?array $response, string $key): array
{
    if (isset($response[$key]) && is_array($response[$key])) {
        return $response[$key];
    }

    if (isset($response['data'][$key]) && is_array($response['data'][$key])) {
        return $response['data'][$key];
    }

    return [];
}

function demo_courses(): array
{
    return [
        ['id' => 'course-ads', 'name' => 'Analise e Desenvolvimento de Sistemas'],
        ['id' => 'course-admin', 'name' => 'Administracao'],
        ['id' => 'course-marketing', 'name' => 'Marketing'],
        ['id' => 'course-contabeis', 'name' => 'Ciencias Contabeis'],
    ];
}

function demo_companies(): array
{
    return [
        [
            'id' => 'company-tech-local',
            'name' => 'Tech Local Sistemas',
            'cnpj' => '12345678000190',
            'description' => 'Empresa de tecnologia focada em sistemas web, suporte e automacao para negocios locais.',
            'phone' => '44999991000',
            'status' => 'APPROVED',
        ],
        [
            'id' => 'company-agencia-alfa',
            'name' => 'Agencia Alfa Marketing',
            'cnpj' => '22345678000191',
            'description' => 'Agencia de comunicacao que atende marcas da regiao com conteudo, social media e trafego pago.',
            'phone' => '44999992000',
            'status' => 'APPROVED',
        ],
        [
            'id' => 'company-winfo',
            'name' => 'Winfo Solucoes em TI',
            'cnpj' => '32345678000192',
            'description' => 'Consultoria de infraestrutura, redes, manutencao e atendimento tecnico para empresas.',
            'phone' => '44999993000',
            'status' => 'APPROVED',
        ],
        [
            'id' => 'company-contabil-prime',
            'name' => 'Contabil Prime',
            'cnpj' => '42345678000193',
            'description' => 'Escritorio contabil ficticio com foco em atendimento empresarial, fiscal e financeiro.',
            'phone' => '44999994000',
            'status' => 'APPROVED',
        ],
    ];
}

function demo_jobs(): array
{
    return [
        [
            'id' => 'job-backend-jr',
            'companyId' => 'company-tech-local',
            'courseId' => 'course-ads',
            'title' => 'Estagio Backend Jr',
            'description' => 'Apoio no desenvolvimento de APIs, manutencao de sistemas internos e integracoes com banco de dados.',
            'area' => 'Tecnologia',
            'requirements' => 'Logica de programacao, PHP ou JavaScript basico e vontade de aprender.',
            'salary' => 1200,
            'location' => 'Umuarama, PR',
            'modality' => 'HYBRID',
            'status' => 'ACTIVE',
            'company' => ['name' => 'Tech Local Sistemas'],
        ],
        [
            'id' => 'job-front-end',
            'companyId' => 'company-tech-local',
            'courseId' => 'course-ads',
            'title' => 'Estagio Front-end',
            'description' => 'Criacao de telas responsivas, ajustes de CSS e apoio na melhoria da experiencia do usuario.',
            'area' => 'Tecnologia',
            'requirements' => 'HTML, CSS, Bootstrap e nocao de JavaScript.',
            'salary' => 1100,
            'location' => 'Umuarama, PR',
            'modality' => 'REMOTE',
            'status' => 'ACTIVE',
            'company' => ['name' => 'Tech Local Sistemas'],
        ],
        [
            'id' => 'job-marketing',
            'companyId' => 'company-agencia-alfa',
            'courseId' => 'course-marketing',
            'title' => 'Estagio em Marketing Digital',
            'description' => 'Planejamento de posts, acompanhamento de campanhas e apoio na criacao de conteudos.',
            'area' => 'Marketing',
            'requirements' => 'Boa escrita, criatividade e familiaridade com redes sociais.',
            'salary' => 900,
            'location' => 'Umuarama, PR',
            'modality' => 'PRESENCIAL',
            'status' => 'ACTIVE',
            'company' => ['name' => 'Agencia Alfa Marketing'],
        ],
        [
            'id' => 'job-suporte',
            'companyId' => 'company-winfo',
            'courseId' => 'course-ads',
            'title' => 'Estagio em Suporte TI',
            'description' => 'Atendimento a usuarios, configuracao de computadores e apoio em infraestrutura.',
            'area' => 'Tecnologia',
            'requirements' => 'Conhecimento basico em Windows, redes e atendimento ao cliente.',
            'salary' => 1050,
            'location' => 'Umuarama, PR',
            'modality' => 'PRESENCIAL',
            'status' => 'ACTIVE',
            'company' => ['name' => 'Winfo Solucoes em TI'],
        ],
        [
            'id' => 'job-contabil',
            'companyId' => 'company-contabil-prime',
            'courseId' => 'course-contabeis',
            'title' => 'Estagio Administrativo Financeiro',
            'description' => 'Organizacao de documentos, lancamentos simples, planilhas e apoio no atendimento a empresas.',
            'area' => 'Administracao',
            'requirements' => 'Excel basico, organizacao e comunicacao clara.',
            'salary' => 1000,
            'location' => 'Umuarama, PR',
            'modality' => 'HYBRID',
            'status' => 'ACTIVE',
            'company' => ['name' => 'Contabil Prime'],
        ],
    ];
}

function demo_students(): array
{
    return [
        [
            'id' => 'student-ana',
            'userId' => 'user-ana',
            'name' => 'Ana Clara Souza',
            'ra' => '2026001',
            'cpf' => '12345678901',
            'phone' => '44988881001',
            'isEligible' => true,
            'course' => 'Analise e Desenvolvimento de Sistemas',
            'status' => 'Em andamento',
        ],
        [
            'id' => 'student-wesley',
            'userId' => 'user-wesley',
            'name' => 'Wesley Kenji Ito Hidehira',
            'ra' => '2026002',
            'cpf' => '23456789012',
            'phone' => '44988882002',
            'isEligible' => true,
            'course' => 'Analise e Desenvolvimento de Sistemas',
            'status' => 'Em andamento',
        ],
        [
            'id' => 'student-mariana',
            'userId' => 'user-mariana',
            'name' => 'Mariana Lima Pereira',
            'ra' => '2026003',
            'cpf' => '34567890123',
            'phone' => '44988883003',
            'isEligible' => true,
            'course' => 'Marketing',
            'status' => 'Finalizado',
        ],
        [
            'id' => 'student-lucas',
            'userId' => 'user-lucas',
            'name' => 'Lucas Almeida Rocha',
            'ra' => '2026004',
            'cpf' => '45678901234',
            'phone' => '44988884004',
            'isEligible' => false,
            'course' => 'Administracao',
            'status' => 'Nao finalizado',
        ],
    ];
}
