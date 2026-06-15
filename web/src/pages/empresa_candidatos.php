<?php
require_once __DIR__ . '/../api.php';
require_once __DIR__ . '/../classes/Vaga.php';
require_once __DIR__ . '/_empresa_menu.php';

$token = empresa_exigir_login();
$vagaId = trim($_GET['vaga_id'] ?? '');
$erro = '';
$sucesso = '';

if ($vagaId === '') {
    $jobsData = api_get('/company/jobs?page=1&limit=50', $token);
    $jobs = api_items($jobsData, 'jobs');
    $vagas = [];
    $candidatosPorVaga = [];
    $totalCandidatos = 0;

    foreach ($jobs as $item) {
        $vaga = new Vaga($item);
        $vagas[] = $vaga;

        $appsData = api_get('/company/jobs/' . urlencode($vaga->getId()) . '/applications?page=1&limit=50', $token);
        $apps = api_items($appsData, 'applications');
        $candidatosPorVaga[$vaga->getId()] = count($apps);
        $totalCandidatos += count($apps);
    }
    ?>

    <section class="empresa-area-page">
      <div class="container py-4 py-lg-5">
        <?php empresa_menu('candidatos'); ?>

        <div class="empresa-page-heading">
          <div>
            <span class="empresa-eyebrow">Triagem de candidatos</span>
            <h1>Candidatos por vaga</h1>
            <p>Escolha uma vaga para analisar os alunos inscritos e atualizar o andamento.</p>
          </div>

          <a href="<?= BASE ?>index.php?page=empresa-vaga-form" class="btn btn-primary">
            <i class="bi bi-plus-lg me-1"></i> Nova vaga
          </a>
        </div>

        <div class="empresa-stats-grid empresa-stats-compact">
          <div class="empresa-stat">
            <span class="empresa-stat-icon is-blue"><i class="bi bi-briefcase"></i></span>
            <div>
              <strong><?= count($vagas) ?></strong>
              <span>vagas cadastradas</span>
            </div>
          </div>
          <div class="empresa-stat">
            <span class="empresa-stat-icon is-yellow"><i class="bi bi-people"></i></span>
            <div>
              <strong><?= $totalCandidatos ?></strong>
              <span>candidatos recebidos</span>
            </div>
          </div>
        </div>

        <?php if (empty($vagas)): ?>
          <div class="empresa-empty">
            <i class="bi bi-briefcase"></i>
            <h2>Nenhuma vaga cadastrada</h2>
            <p>Cadastre uma vaga para comecar a receber candidaturas dos alunos.</p>
            <a href="<?= BASE ?>index.php?page=empresa-vaga-form" class="btn btn-primary">Publicar vaga</a>
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
                    <span><i class="bi bi-people"></i> <?= $qtdCandidatos ?> candidato<?= $qtdCandidatos !== 1 ? 's' : '' ?></span>
                  </div>
                </div>

                <div class="empresa-job-actions">
                  <a href="<?= BASE ?>index.php?page=empresa-candidatos&vaga_id=<?= urlencode($vaga->getId()) ?>"
                     class="btn btn-outline-primary btn-sm">
                    <i class="bi bi-people me-1"></i> Ver candidatos
                  </a>
                  <a href="<?= BASE ?>index.php?page=empresa-vaga-form&vaga_id=<?= urlencode($vaga->getId()) ?>"
                     class="btn btn-outline-secondary btn-sm">
                    <i class="bi bi-pencil me-1"></i> Editar vaga
                  </a>
                </div>
              </article>
            <?php endforeach; ?>
          </div>
        <?php endif; ?>
      </div>
    </section>
    <?php
    return;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['candidatura_id'], $_POST['status'])) {
    $candidaturaId = trim($_POST['candidatura_id']);
    $novoStatus = trim($_POST['status']);

    $data = api_patch_json(
        '/company/jobs/' . urlencode($vagaId) . '/applications/' . urlencode($candidaturaId) . '/status',
        ['status' => $novoStatus],
        $token
    );

    if ($data['success'] ?? false) {
        $sucesso = 'Status atualizado com sucesso.';
    } else {
        $erro = $data['message'] ?? 'Erro ao atualizar o status.';
    }
}

$vagaData = api_get('/company/jobs/' . urlencode($vagaId), $token);
$vaga = $vagaData['data'] ?? [];
$tituloVaga = $vaga['title'] ?? 'Vaga';

$appsData = api_get('/company/jobs/' . urlencode($vagaId) . '/applications?page=1&limit=50', $token);
$candidaturas = api_items($appsData, 'applications');
$totalCandidaturas = count($candidaturas);

$contadores = [
    'PENDING' => 0,
    'ANALYSING' => 0,
    'APPROVED' => 0,
    'REJECTED' => 0,
];

foreach ($candidaturas as $candidatura) {
    $status = $candidatura['status'] ?? 'PENDING';
    if (isset($contadores[$status])) {
        $contadores[$status]++;
    }
}
?>

<section class="empresa-area-page">
  <div class="container py-4 py-lg-5">
    <?php empresa_menu('candidatos'); ?>

    <div class="empresa-page-heading">
      <div>
        <span class="empresa-eyebrow">Triagem de candidatos</span>
        <h1><?= htmlspecialchars($tituloVaga) ?></h1>
        <p>Analise os alunos interessados e atualize o andamento de cada candidatura.</p>
      </div>

      <a href="<?= BASE ?>index.php?page=empresa-dashboard#empresa-vagas" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left me-1"></i> Voltar as vagas
      </a>
    </div>

    <?php if ($sucesso): ?>
      <div class="alert alert-success alert-dismissible fade show empresa-alert" role="alert">
        <?= htmlspecialchars($sucesso) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
    <?php endif; ?>

    <?php if ($erro): ?>
      <div class="alert alert-danger alert-dismissible fade show empresa-alert" role="alert">
        <?= htmlspecialchars($erro) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
      </div>
    <?php endif; ?>

    <div class="empresa-stats-grid empresa-stats-compact">
      <div class="empresa-stat">
        <span class="empresa-stat-icon is-blue"><i class="bi bi-people"></i></span>
        <div>
          <strong><?= $totalCandidaturas ?></strong>
          <span>candidatos</span>
        </div>
      </div>
      <div class="empresa-stat">
        <span class="empresa-stat-icon is-gray"><i class="bi bi-clock"></i></span>
        <div>
          <strong><?= $contadores['PENDING'] ?></strong>
          <span>pendentes</span>
        </div>
      </div>
      <div class="empresa-stat">
        <span class="empresa-stat-icon is-yellow"><i class="bi bi-hourglass-split"></i></span>
        <div>
          <strong><?= $contadores['ANALYSING'] ?></strong>
          <span>em analise</span>
        </div>
      </div>
      <div class="empresa-stat">
        <span class="empresa-stat-icon is-green"><i class="bi bi-check2-circle"></i></span>
        <div>
          <strong><?= $contadores['APPROVED'] ?></strong>
          <span>aprovados</span>
        </div>
      </div>
    </div>

    <?php if (empty($candidaturas)): ?>
      <div class="empresa-empty">
        <i class="bi bi-person-plus"></i>
        <h2>Nenhum candidato ainda</h2>
        <p>Quando os alunos se candidatarem, eles aparecem aqui para a empresa analisar.</p>
      </div>
    <?php else: ?>
      <div class="empresa-candidate-list">
        <?php foreach ($candidaturas as $candidatura): ?>
          <?php
          $aluno = $candidatura['student'] ?? [];
          $status = $candidatura['status'] ?? 'PENDING';
          $curso = $aluno['courses'][0]['course']['name'] ?? ($aluno['course'] ?? 'Curso nao informado');
          $resumo = $aluno['summary'] ?? 'Resumo profissional ainda nao cadastrado.';
          ?>
          <article class="empresa-candidate-row">
            <div class="empresa-candidate-avatar">
              <?= htmlspecialchars(empresa_iniciais($aluno['name'] ?? 'Aluno')) ?>
            </div>

            <div class="empresa-candidate-info">
              <div class="empresa-candidate-title">
                <div>
                  <h2><?= htmlspecialchars($aluno['name'] ?? 'Aluno') ?></h2>
                  <p><?= htmlspecialchars($curso) ?></p>
                </div>
                <span class="empresa-pill <?= empresa_candidatura_status_classe($status) ?>">
                  <?= empresa_candidatura_status_label($status) ?>
                </span>
              </div>

              <p class="empresa-candidate-summary"><?= htmlspecialchars(empresa_resumo($resumo, 180)) ?></p>

              <div class="empresa-meta-list">
                <span><i class="bi bi-card-text"></i> RA: <?= htmlspecialchars($aluno['ra'] ?? '-') ?></span>
                <?php if (!empty($aluno['phone'])): ?>
                  <span><i class="bi bi-telephone"></i> <?= htmlspecialchars($aluno['phone']) ?></span>
                <?php endif; ?>
                <span><i class="bi bi-calendar"></i> Candidatou em <?= empresa_data_curta($candidatura['createdAt'] ?? null) ?></span>
              </div>
            </div>

            <div class="empresa-candidate-actions">
              <form method="POST"
                    action="<?= BASE ?>index.php?page=empresa-candidatos&vaga_id=<?= urlencode($vagaId) ?>">
                <input type="hidden" name="candidatura_id" value="<?= htmlspecialchars($candidatura['id'] ?? '') ?>">
                <label class="visually-hidden" for="status-<?= htmlspecialchars($candidatura['id'] ?? '') ?>">Status</label>
                <select id="status-<?= htmlspecialchars($candidatura['id'] ?? '') ?>" name="status" class="form-select form-select-sm">
                  <option value="PENDING" <?= $status === 'PENDING' ? 'selected' : '' ?>>Pendente</option>
                  <option value="ANALYSING" <?= $status === 'ANALYSING' ? 'selected' : '' ?>>Em analise</option>
                  <option value="APPROVED" <?= $status === 'APPROVED' ? 'selected' : '' ?>>Aprovada</option>
                  <option value="REJECTED" <?= $status === 'REJECTED' ? 'selected' : '' ?>>Rejeitada</option>
                  <option value="CANCELLED" <?= $status === 'CANCELLED' ? 'selected' : '' ?>>Cancelada</option>
                </select>
                <button type="submit" class="btn btn-primary btn-sm">Salvar</button>
              </form>
            </div>
          </article>
        <?php endforeach; ?>
      </div>
    <?php endif; ?>
  </div>
</section>
