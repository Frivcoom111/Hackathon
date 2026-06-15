<?php
require_once __DIR__ . '/../../classes/Empresa.php';
require_once __DIR__ . '/../../classes/Vaga.php';
require_once __DIR__ . '/../../api.php';
require_once __DIR__ . '/_empresa_menu.php';

$token = empresa_exigir_login();

$empresaData = api_get('/company/profile', $token);
$empresa = !empty($empresaData['data']) ? new Empresa($empresaData['data']) : null;

$jobsData = api_get('/company/jobs?page=1&limit=50', $token);
$jobs = api_items($jobsData, 'jobs');

if ($jobs === [] && !empty($jobsData['data']) && is_array($jobsData['data'])) {
    $jobs = $jobsData['data'];
}

$vagas = [];
$candidatosPorVaga = [];
$totalCandidatos = 0;
$vagasAtivas = 0;
$vagasPausadas = 0;
$vagasEncerradas = 0;

foreach ($jobs as $item) {
    $vaga = new Vaga($item);
    $vagas[] = $vaga;

    if ($vaga->getStatus() === 'ACTIVE') {
        $vagasAtivas++;
    } elseif ($vaga->getStatus() === 'PAUSED') {
        $vagasPausadas++;
    } elseif ($vaga->getStatus() === 'CLOSED') {
        $vagasEncerradas++;
    }

    $appsData = api_get('/company/jobs/' . urlencode($vaga->getId()) . '/applications?page=1&limit=50', $token);
    $apps = api_items($appsData, 'applications');
    $candidatosPorVaga[$vaga->getId()] = count($apps);
    $totalCandidatos += count($apps);
}

$totalVagas = count($vagas);
$nomeEmpresa = $empresa ? $empresa->getNome() : 'Empresa';
?>

<section class="empresa-area-page">
  <div class="container py-4 py-lg-5">
    <?php empresa_menu('painel'); ?>

    <?php if (!$empresa): ?>
      <div class="empresa-empty">
        <i class="bi bi-building-x"></i>
        <h2>Nao foi possivel carregar a empresa</h2>
        <p>Entre novamente para atualizar os dados da sessao.</p>
        <a href="<?= BASE ?>index.php?page=login" class="btn btn-primary">Fazer login</a>
      </div>
    <?php else: ?>
      <div class="empresa-hero-card">
        <div class="empresa-hero-main">
          <div class="empresa-avatar" aria-hidden="true">
            <?= htmlspecialchars(empresa_iniciais($empresa->getNome())) ?>
          </div>

          <div class="empresa-hero-copy">
            <span class="empresa-eyebrow">Area da empresa</span>
            <h1><?= htmlspecialchars($empresa->getNome()) ?></h1>
            <p><?= htmlspecialchars(empresa_resumo($empresa->getDescricao(), 170)) ?></p>

            <div class="empresa-meta-list">
              <span><i class="bi bi-upc-scan"></i> <?= $empresa->getCnpjFormatado() ?></span>
              <?php if ($empresa->getTelefone()): ?>
                <span><i class="bi bi-telephone"></i> <?= htmlspecialchars($empresa->getTelefone()) ?></span>
              <?php endif; ?>
              <span><i class="bi bi-calendar-check"></i> Desde <?= empresa_data_curta($empresa->getCriadoEm()) ?></span>
            </div>
          </div>
        </div>

        <div class="empresa-hero-actions">
          <span class="empresa-pill <?= empresa_status_classe($empresa->getStatus()) ?>">
            <?= empresa_status_label($empresa->getStatus()) ?>
          </span>
          <a href="<?= BASE ?>index.php?page=empresa-vaga-form" class="btn btn-warning fw-semibold">
            <i class="bi bi-plus-lg me-1"></i> Publicar vaga
          </a>
        </div>
      </div>

      <div class="empresa-stats-grid">
        <div class="empresa-stat">
          <span class="empresa-stat-icon is-blue"><i class="bi bi-briefcase"></i></span>
          <div>
            <strong><?= $totalVagas ?></strong>
            <span>vagas cadastradas</span>
          </div>
        </div>

        <div class="empresa-stat">
          <span class="empresa-stat-icon is-green"><i class="bi bi-check2-circle"></i></span>
          <div>
            <strong><?= $vagasAtivas ?></strong>
            <span>vagas ativas</span>
          </div>
        </div>

        <div class="empresa-stat">
          <span class="empresa-stat-icon is-yellow"><i class="bi bi-people"></i></span>
          <div>
            <strong><?= $totalCandidatos ?></strong>
            <span>candidaturas recebidas</span>
          </div>
        </div>

        <div class="empresa-stat">
          <span class="empresa-stat-icon is-gray"><i class="bi bi-pause-circle"></i></span>
          <div>
            <strong><?= $vagasPausadas + $vagasEncerradas ?></strong>
            <span>pausadas ou encerradas</span>
          </div>
        </div>
      </div>

      <div class="empresa-layout">
        <main class="empresa-main">
          <div class="empresa-section-head" id="empresa-vagas">
            <div>
              <span class="empresa-eyebrow">Gestao de vagas</span>
              <h2>Minhas vagas</h2>
              <p>Acompanhe publicacoes, edite oportunidades e veja os candidatos de cada vaga.</p>
            </div>

            <a href="<?= BASE ?>index.php?page=empresa-vaga-form" class="btn btn-primary">
              <i class="bi bi-plus-lg me-1"></i> Nova vaga
            </a>
          </div>

          <?php if (empty($vagas)): ?>
            <div class="empresa-empty is-small">
              <i class="bi bi-briefcase"></i>
              <h3>Nenhuma vaga cadastrada</h3>
              <p>Cadastre a primeira oportunidade para comecar a receber alunos interessados.</p>
              <a href="<?= BASE ?>index.php?page=empresa-vaga-form" class="btn btn-outline-primary">Criar primeira vaga</a>
            </div>
          <?php else: ?>
            <div class="empresa-job-list">
              <?php foreach ($vagas as $vaga): ?>
                <?php $qtdCandidatos = $candidatosPorVaga[$vaga->getId()] ?? 0; ?>
                <article class="empresa-job-row">
                  <div class="empresa-job-status">
                    <span class="empresa-pill <?= empresa_vaga_status_classe($vaga->getStatus()) ?>">
                      <?= empresa_vaga_status_label($vaga->getStatus()) ?>
                    </span>
                    <span class="empresa-job-modality"><?= $vaga->getModalidadeLabel() ?></span>
                  </div>

                  <div class="empresa-job-body">
                    <h3><?= htmlspecialchars($vaga->getTitulo()) ?></h3>
                    <p><?= htmlspecialchars(empresa_resumo($vaga->getDescricao(), 140)) ?></p>

                    <div class="empresa-meta-list">
                      <span><i class="bi bi-geo-alt"></i> <?= htmlspecialchars($vaga->getLocalizacao()) ?></span>
                      <span><i class="bi bi-layers"></i> <?= htmlspecialchars($vaga->getArea()) ?></span>
                      <span><i class="bi bi-cash-coin"></i> <?= $vaga->getSalarioFormatado() ?></span>
                      <span><i class="bi bi-people"></i> <?= $qtdCandidatos ?> candidato<?= $qtdCandidatos !== 1 ? 's' : '' ?></span>
                    </div>
                  </div>

                  <div class="empresa-job-actions">
                    <a href="<?= BASE ?>index.php?page=empresa-candidatos&vaga_id=<?= urlencode($vaga->getId()) ?>"
                       class="btn btn-outline-primary btn-sm">
                      <i class="bi bi-people me-1"></i> Candidatos
                    </a>
                    <a href="<?= BASE ?>index.php?page=empresa-vaga-form&vaga_id=<?= urlencode($vaga->getId()) ?>"
                       class="btn btn-outline-secondary btn-sm">
                      <i class="bi bi-pencil me-1"></i> Editar
                    </a>
                  </div>
                </article>
              <?php endforeach; ?>
            </div>
          <?php endif; ?>
        </main>

        <aside class="empresa-side">
          <div class="empresa-panel">
            <span class="empresa-eyebrow">Resumo rapido</span>
            <h2><?= htmlspecialchars($nomeEmpresa) ?></h2>
            <p>Use este painel para publicar vagas, acompanhar interessados e manter as oportunidades atualizadas.</p>

            <div class="empresa-side-list">
              <a href="<?= BASE ?>index.php?page=empresa-vaga-form">
                <i class="bi bi-plus-square"></i>
                <span>Publicar nova vaga</span>
              </a>
              <a href="<?= BASE ?>index.php?page=empresa-dashboard#empresa-vagas">
                <i class="bi bi-briefcase"></i>
                <span>Ver vagas cadastradas</span>
              </a>
              <a href="<?= BASE ?>index.php?page=vagas">
                <i class="bi bi-search"></i>
                <span>Ver portal publico</span>
              </a>
            </div>
          </div>

          <div class="empresa-panel">
            <span class="empresa-eyebrow">Fluxo da empresa</span>
            <ol class="empresa-flow">
              <li><span>1</span> Publique uma vaga completa.</li>
              <li><span>2</span> Alunos se candidatam pelo portal.</li>
              <li><span>3</span> A empresa avalia e muda o status.</li>
            </ol>
          </div>
        </aside>
      </div>
    <?php endif; ?>
  </div>
</section>

