<?php
require_once __DIR__ . '/../../classes/Candidatura.php';

\App\Auth\Guard::requireCompany($api->jwt());

$vagaId  = trim($_GET['vaga_id'] ?? '');
$erro    = '';
$sucesso = '';

if (!$vagaId) {
    header('Location: ' . BASE . 'index.php?page=empresa-dashboard');
    exit;
}

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['candidatura_id'], $_POST['status'])) {
    $data = $api->companhia()->alterarStatusCandidatura(
        $vagaId,
        trim($_POST['candidatura_id']),
        trim($_POST['status'])
    );
    if ($data['success'] ?? false) {
        $sucesso = 'Status atualizado com sucesso!';
    } else {
        $erro = $data['message'] ?? 'Erro ao atualizar o status.';
    }
}

$respVaga   = $api->companhia()->vaga($vagaId);
$tituloVaga = $respVaga['data']['title'] ?? 'Vaga';

$respApps     = $api->companhia()->candidatos($vagaId);
$itensRaw     = $respApps['data'] ?? [];
$candidaturas = array_map(fn($item) => new Candidatura($item), $itensRaw);
?>

<!-- TOPO -->
<section class="vagas-hero">
  <div class="container">
    <a href="<?= BASE ?>index.php?page=empresa-dashboard" class="btn btn-outline-light btn-sm mb-3">
      <i class="bi bi-arrow-left me-1"></i> Voltar
    </a>
    <h1 class="vagas-hero-titulo">Candidatos</h1>
    <p class="vagas-hero-sub"><?= htmlspecialchars($tituloVaga) ?></p>
  </div>
</section>

<section class="vagas-lista-section">
  <div class="container py-4">

    <?php if ($sucesso): ?>
      <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
        <?= htmlspecialchars($sucesso) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
      </div>
    <?php endif; ?>

    <?php if ($erro): ?>
      <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
        <?= htmlspecialchars($erro) ?>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Fechar"></button>
      </div>
    <?php endif; ?>

    <?php if (empty($candidaturas)): ?>
      <div class="vagas-vazio">
        <i class="bi bi-people"></i>
        <p>Nenhum candidato encontrado para esta vaga.</p>
      </div>

    <?php else: ?>
      <p class="text-secondary mb-3">
        <strong><?= count($candidaturas) ?></strong>
        candidatura<?= count($candidaturas) !== 1 ? 's' : '' ?> encontrada<?= count($candidaturas) !== 1 ? 's' : '' ?>
      </p>

      <div class="row g-4">
        <?php foreach ($itensRaw as $index => $item):
            $candidatura = $candidaturas[$index];
            $nomeAluno   = $item['student']['name'] ?? 'Aluno';
            $raAluno     = $item['student']['ra']   ?? '';
        ?>
          <div class="col-lg-6 col-12">
            <div class="vaga-card">
              <div class="vaga-card-top">
                <div class="empresa-logo">
                  <div class="empresa-logo-placeholder" style="display:flex;">
                    <i class="bi bi-person fs-4"></i>
                  </div>
                </div>
                <span class="badge <?= $candidatura->getStatusBadgeClass() ?> ms-auto">
                  <?= $candidatura->getStatusLabel() ?>
                </span>
              </div>

              <h5 class="vaga-titulo"><?= htmlspecialchars($nomeAluno) ?></h5>

              <div class="vaga-infos">
                <?php if ($raAluno): ?>
                  <span><i class="bi bi-card-text"></i> RA: <?= htmlspecialchars($raAluno) ?></span>
                <?php endif; ?>
                <span>
                  <i class="bi bi-calendar"></i>
                  Candidatou-se em <?= date('d/m/Y', strtotime($candidatura->getCriadoEm())) ?>
                </span>
                <?php if ($candidatura->getCurriculo()): ?>
                  <span>
                    <i class="bi bi-file-earmark-text"></i>
                    <a href="<?= htmlspecialchars($candidatura->getCurriculo()) ?>" target="_blank">
                      Ver currículo
                    </a>
                  </span>
                <?php endif; ?>
              </div>

              <?php if (!$candidatura->isRejeitada() && !$candidatura->isCancelada()): ?>
                <div class="vaga-footer">
                  <form method="POST"
                        action="<?= BASE ?>index.php?page=empresa-candidatos&vaga_id=<?= htmlspecialchars($vagaId) ?>">
                    <input type="hidden" name="candidatura_id" value="<?= $candidatura->getId() ?>">
                    <div class="d-flex gap-2 align-items-center">
                      <select name="status" class="form-select form-select-sm">
                        <option value="<?= Candidatura::STATUS_PENDENTE ?>"
                          <?= $candidatura->isPendente() ? 'selected' : '' ?>>Pendente</option>
                        <option value="<?= Candidatura::STATUS_ANALISANDO ?>"
                          <?= $candidatura->getStatus() === Candidatura::STATUS_ANALISANDO ? 'selected' : '' ?>>Em análise</option>
                        <option value="<?= Candidatura::STATUS_APROVADA ?>"
                          <?= $candidatura->isAprovada() ? 'selected' : '' ?>>Aprovada</option>
                        <option value="<?= Candidatura::STATUS_REJEITADA ?>">Rejeitar</option>
                      </select>
                      <button type="submit" class="btn btn-primary btn-sm px-3">Salvar</button>
                    </div>
                  </form>
                </div>
              <?php endif; ?>

            </div>
          </div>
        <?php endforeach; ?>
      </div>
    <?php endif; ?>

  </div>
</section>
