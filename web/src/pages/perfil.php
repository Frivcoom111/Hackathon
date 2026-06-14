<?php
// Carrega as classes necessárias
require_once __DIR__ . '/../classes/Aluno.php';
require_once __DIR__ . '/../classes/Candidatura.php';

// Se não estiver logado, redireciona para o login
if (empty($_SESSION['token'])) {
    header('Location: ' . BASE . 'index.php?page=login');
    exit;
}

$token = $_SESSION['token'];

// ── Cancela candidatura se o aluno clicou no botão ───────────────────────────
if ($_SERVER['REQUEST_METHOD'] === 'POST' && ($_POST['acao'] ?? '') === 'cancelar') {
    $id = $_POST['candidatura_id'] ?? '';
    if ($id) {
        $ch = curl_init('http://localhost:3000/student/applications/' . $id);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, 'DELETE');
        curl_setopt($ch, CURLOPT_HTTPHEADER, ['Authorization: Bearer ' . $token]);
        curl_exec($ch);
        curl_close($ch);
    }
    header('Location: ' . BASE . 'index.php?page=perfil');
    exit;
}

// ── Busca o perfil do aluno logado via cURL ───────────────────────────────────
$ch = curl_init('http://localhost:3000/student/profile');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Authorization: Bearer ' . $token]);
$resp = curl_exec($ch);
curl_close($ch);

$aluno = null;
$data  = json_decode($resp, true);
if (!empty($data['data'])) {
    $aluno = new Aluno($data['data']);
}

// ── Busca as candidaturas do aluno logado via cURL ────────────────────────────
$ch = curl_init('http://localhost:3000/student/applications?page=1&limit=20');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Authorization: Bearer ' . $token]);
$resp = curl_exec($ch);
curl_close($ch);

$candidaturas = [];
$dataCandy    = json_decode($resp, true);
foreach ($dataCandy['data'] ?? [] as $item) {
    $candidaturas[] = new Candidatura($item);
}
?>

<!-- TOPO DA PÁGINA -->
<section class="vagas-hero">
  <div class="container">
    <h1 class="vagas-hero-titulo">Meu Perfil</h1>
    <p class="vagas-hero-sub">Suas informações e candidaturas</p>
  </div>
</section>

<section class="vagas-lista-section">
  <div class="container py-4">

    <?php if (!$aluno): ?>
      <!-- API ainda não implementou a rota ou token inválido -->
      <div class="text-center py-5">
        <i class="bi bi-person-x fs-2 text-secondary"></i>
        <p class="text-secondary mt-2">Não foi possível carregar seu perfil.</p>
        <a href="<?= BASE ?>index.php?page=login" class="btn btn-outline-primary btn-sm">Fazer login novamente</a>
      </div>

    <?php else: ?>

      <!-- DADOS DO ALUNO -->
      <div class="row g-4 mb-5">
        <div class="col-lg-4">
          <div class="vaga-card">
            <div class="vaga-card-top">
              <div class="empresa-logo">
                <div class="empresa-logo-placeholder" style="display:flex;">
                  <i class="bi bi-person fs-4"></i>
                </div>
              </div>
            </div>
            <h5 class="vaga-titulo"><?= htmlspecialchars($aluno->getNome()) ?></h5>
            <div class="vaga-infos">
              <span><i class="bi bi-card-text"></i> RA: <?= htmlspecialchars($aluno->getRa()) ?></span>
              <span><i class="bi bi-person-badge"></i> CPF: <?= $aluno->getCpfFormatado() ?></span>
              <?php if ($aluno->getTelefone()): ?>
                <span><i class="bi bi-telephone"></i> <?= htmlspecialchars($aluno->getTelefone()) ?></span>
              <?php endif; ?>
              <span>
                <i class="bi bi-patch-check<?= $aluno->isElegivel() ? '-fill text-success' : '' ?>"></i>
                <?= $aluno->isElegivel() ? 'Elegível para estágio' : 'Não elegível' ?>
              </span>
            </div>
            <?php if ($aluno->getCurriculo()): ?>
              <div class="vaga-footer">
                <a href="<?= htmlspecialchars($aluno->getCurriculo()) ?>" target="_blank"
                   class="btn btn-outline-primary btn-sm">
                  <i class="bi bi-file-earmark-text me-1"></i> Ver currículo
                </a>
              </div>
            <?php endif; ?>
          </div>
        </div>
      </div>

      <!-- CANDIDATURAS DO ALUNO -->
      <h4 class="fw-semibold mb-3">Minhas Candidaturas</h4>

      <?php if (empty($candidaturas)): ?>
        <div class="vagas-vazio">
          <i class="bi bi-briefcase"></i>
          <p>Você ainda não se candidatou a nenhuma vaga.</p>
          <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-outline-primary btn-sm">Ver vagas disponíveis</a>
        </div>

      <?php else: ?>
        <div class="row g-4">
          <?php foreach ($candidaturas as $candidatura): ?>
            <div class="col-lg-6 col-12">
              <div class="vaga-card">
                <div class="vaga-card-top">
                  <span class="badge <?= $candidatura->getStatusBadgeClass() ?>">
                    <?= $candidatura->getStatusLabel() ?>
                  </span>
                </div>

                <p class="text-secondary small mb-1">
                  Candidatura enviada em
                  <?= date('d/m/Y', strtotime($candidatura->getCriadoEm())) ?>
                </p>

                <?php if ($candidatura->getCartaApresentacao()): ?>
                  <p class="descricao"><?= htmlspecialchars($candidatura->getCartaApresentacao()) ?></p>
                <?php endif; ?>

                <!-- Cancelar só permite se estiver pendente ou em análise -->
                <?php if ($candidatura->isPendente() || $candidatura->getStatus() === 'ANALYSING'): ?>
                  <div class="vaga-footer">
                    <form method="POST" action="<?= BASE ?>index.php?page=perfil">
                      <input type="hidden" name="acao"          value="cancelar">
                      <input type="hidden" name="candidatura_id" value="<?= $candidatura->getId() ?>">
                      <button type="submit" class="btn btn-outline-danger btn-sm"
                              onclick="return confirm('Cancelar esta candidatura?')">
                        Cancelar candidatura
                      </button>
                    </form>
                  </div>
                <?php endif; ?>
              </div>
            </div>
          <?php endforeach; ?>
        </div>
      <?php endif; ?>

    <?php endif; ?>

  </div>
</section>
