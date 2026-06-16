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
        <?php foreach ($candidaturas as $candidatura):
            $nomeAluno = $candidatura->getAlunoNome();
            $raAluno   = $candidatura->getAlunoRa();
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
              </div>

              <div class="mt-1">
                <button type="button" class="btn btn-outline-primary btn-sm" onclick="abrirCandidato(this)"
                  data-nome="<?= htmlspecialchars($nomeAluno) ?>"
                  data-ra="<?= htmlspecialchars($raAluno) ?>"
                  data-email="<?= htmlspecialchars($candidatura->getAlunoEmail()) ?>"
                  data-telefone="<?= htmlspecialchars($candidatura->getAlunoTelefone()) ?>"
                  data-curso="<?= htmlspecialchars($candidatura->getAlunoCurso()) ?>"
                  data-status="<?= htmlspecialchars($candidatura->getStatusLabel()) ?>"
                  data-status-classe="<?= htmlspecialchars($candidatura->getStatusBadgeClass()) ?>"
                  data-data="<?= date('d/m/Y', strtotime($candidatura->getCriadoEm())) ?>"
                  data-tem-curriculo="<?= $candidatura->temCurriculo() ? '1' : '0' ?>"
                  data-curriculo-url="<?= BASE ?>index.php?page=curriculo-candidato&job=<?= urlencode($vagaId) ?>&app=<?= urlencode($candidatura->getId()) ?>">
                  <i class="bi bi-eye me-1"></i> Ver detalhes
                </button>
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

<!-- MODAL: detalhes do candidato -->
<div class="modal fade" id="modal-candidato" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="cand-nome">Candidato</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
      </div>
      <div class="modal-body">
        <p class="mb-3">
          <span class="badge" id="cand-status"></span>
          <span class="text-secondary ms-2"><i class="bi bi-calendar"></i> <span id="cand-data"></span></span>
        </p>
        <ul class="candidato-dados">
          <li><i class="bi bi-card-text"></i> <strong>RA:</strong> <span id="cand-ra"></span></li>
          <li><i class="bi bi-mortarboard"></i> <strong>Curso:</strong> <span id="cand-curso"></span></li>
          <li><i class="bi bi-envelope"></i> <strong>E-mail:</strong> <span id="cand-email"></span></li>
          <li><i class="bi bi-telephone"></i> <strong>Telefone:</strong> <span id="cand-telefone"></span></li>
        </ul>
      </div>
      <div class="modal-footer">
        <a href="#" id="cand-curriculo" class="btn btn-primary" target="_blank" rel="noopener">
          <i class="bi bi-download me-1"></i> Baixar currículo
        </a>
        <span id="cand-sem-curriculo" class="text-secondary small">Candidato sem currículo.</span>
        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Fechar</button>
      </div>
    </div>
  </div>
</div>

<script>
function abrirCandidato(btn) {
  const d = btn.dataset;
  const txt = (id, v) => { document.getElementById(id).textContent = v || '—'; };

  document.getElementById('cand-nome').textContent = d.nome;
  txt('cand-ra', d.ra);
  txt('cand-curso', d.curso);
  txt('cand-email', d.email);
  txt('cand-telefone', d.telefone);
  txt('cand-data', d.data);

  const status = document.getElementById('cand-status');
  status.textContent = d.status;
  status.className = 'badge ' + d.statusClasse;

  const link = document.getElementById('cand-curriculo');
  const semCv = document.getElementById('cand-sem-curriculo');
  if (d.temCurriculo === '1') {
    link.href = d.curriculoUrl;
    link.style.display = '';
    semCv.style.display = 'none';
  } else {
    link.style.display = 'none';
    semCv.style.display = '';
  }

  new bootstrap.Modal(document.getElementById('modal-candidato')).show();
}
</script>
