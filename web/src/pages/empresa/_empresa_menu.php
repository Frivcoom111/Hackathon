<?php

function empresa_exigir_login(): string
{
    if (empty($_SESSION['token']) || ($_SESSION['role'] ?? '') !== 'empresa') {
        header('Location: ' . BASE . 'index.php?page=login');
        exit;
    }

    return $_SESSION['token'];
}

function empresa_iniciais(string $nome): string
{
    $partes = preg_split('/\s+/', trim($nome)) ?: [];
    $iniciais = '';

    foreach ($partes as $parte) {
        if ($parte !== '') {
            $iniciais .= strtoupper(substr($parte, 0, 1));
        }

        if (strlen($iniciais) >= 2) {
            break;
        }
    }

    return $iniciais ?: 'EA';
}

function empresa_mapear(string $status, array $mapa, string $padrao): string
{
    return $mapa[$status] ?? $padrao;
}

function empresa_status_label(string $status): string
{
    return empresa_mapear($status, ['APPROVED' => 'Aprovada', 'ANALYSING' => 'Em analise', 'BLOCKED' => 'Bloqueada'], 'Pendente');
}

function empresa_status_classe(string $status): string
{
    return empresa_mapear($status, ['APPROVED' => 'is-success', 'ANALYSING' => 'is-warning', 'BLOCKED' => 'is-danger'], 'is-info');
}

function empresa_vaga_status_label(string $status): string
{
    return empresa_mapear($status, ['ACTIVE' => 'Ativa', 'PAUSED' => 'Pausada', 'CLOSED' => 'Encerrada'], $status);
}

function empresa_vaga_status_classe(string $status): string
{
    return empresa_mapear($status, ['ACTIVE' => 'is-success', 'PAUSED' => 'is-warning', 'CLOSED' => 'is-muted'], 'is-info');
}

function empresa_candidatura_status_label(string $status): string
{
    return empresa_mapear($status, ['ANALYSING' => 'Em analise', 'APPROVED' => 'Aprovada', 'REJECTED' => 'Rejeitada', 'CANCELLED' => 'Cancelada'], 'Pendente');
}

function empresa_candidatura_status_classe(string $status): string
{
    return empresa_mapear($status, ['APPROVED' => 'is-success', 'REJECTED' => 'is-danger', 'CANCELLED' => 'is-muted', 'ANALYSING' => 'is-warning'], 'is-info');
}

function empresa_data_curta(?string $data): string
{
    $timestamp = $data ? strtotime($data) : false;
    return $timestamp ? date('d/m/Y', $timestamp) : '-';
}

function empresa_resumo(?string $texto, int $limite = 120): string
{
    $texto = trim((string)$texto);

    if ($texto === '') {
        return 'Sem descricao cadastrada.';
    }

    $tamanho = function_exists('mb_strlen') ? mb_strlen($texto) : strlen($texto);

    if ($tamanho <= $limite) {
        return $texto;
    }

    $corte = function_exists('mb_substr') ? mb_substr($texto, 0, $limite - 3) : substr($texto, 0, $limite - 3);
    return rtrim($corte) . '...';
}

function empresa_menu(string $ativo = 'painel'): void
{
    $links = [
        'painel' => ['Painel', 'bi-speedometer2', BASE . 'index.php?page=empresa-dashboard'],
        'vagas' => ['Minhas vagas', 'bi-briefcase', BASE . 'index.php?page=empresa-dashboard#empresa-vagas'],
        'nova-vaga' => ['Nova vaga', 'bi-plus-square', BASE . 'index.php?page=empresa-vaga-form'],
        'candidatos' => ['Candidatos', 'bi-people', BASE . 'index.php?page=empresa-candidatos'],
    ];
    ?>
    <nav class="empresa-menu" aria-label="Menu da empresa">
      <?php foreach ($links as $chave => [$label, $icone, $url]): ?>
        <a class="empresa-menu-link <?= $ativo === $chave ? 'active' : '' ?>" href="<?= $url ?>">
          <i class="bi <?= $icone ?>"></i>
          <span><?= $label ?></span>
        </a>
      <?php endforeach; ?>
    </nav>
    <?php
}
