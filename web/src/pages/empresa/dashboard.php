<?php
require_once __DIR__ . '/../../classes/Empresa.php';
require_once __DIR__ . '/../../classes/Vaga.php';

if (empty($_SESSION['token'])) {
    header('Location: ' . BASE . 'index.php?page=login');
    exit;
}

$token = $_SESSION['token'];

// ── Busca o perfil da empresa logada ─────────────────────────────────────────
$ch = curl_init('http://localhost:3000/company/profile');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Authorization: Bearer ' . $token]);
$resp = curl_exec($ch);
curl_close($ch);

$empresa = null;
$data = json_decode($resp, true);
if (!empty($data['data'])) {
    $empresa = new Empresa($data['data']);
}

// ── Busca as vagas da empresa ─────────────────────────────────────────────────
$ch = curl_init('http://localhost:3000/company/jobs?page=1&limit=50');
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Authorization: Bearer ' . $token]);
$resp = curl_exec($ch);
curl_close($ch);

$vagas = [];
$dataVagas = json_decode($resp, true);
foreach ($dataVagas['data'] ?? [] as $item) {
    $vagas[] = new Vaga($item);
}

// ── Label e badge de status da vaga ──────────────────────────────────────────
function statusVagaLabel(string $status): string {
    return match($status) {
        'ACTIVE' => 'Ativa',
        'PAUSED' => 'Pausada',
        'CLOSED' => 'Encerrada',
        default  => $status,
    };
}

function statusVagaBadge(string $status): string {
    return match($status) {
        'ACTIVE' => 'badge bg-success',
        'PAUSED' => 'badge bg-warning text-dark',
        'CLOSED' => 'badge bg-secondary',
        default  => 'badge bg-light text-dark',
    };
}
?>

<!-- TOPO -->
<section class="vagas-hero">
  <div class="container">
    <h1 class="vagas-hero-titulo">Painel da Empresa</h1>
    <p class="vagas-hero-sub">Gerencie suas vagas e acompanhe candidatos</p>
  </div>
</section>

<section class="vagas-lista-section">
  <div class="container py-4">

    <?php if (!$empresa): ?>
      <div class="text-center py-5">
        <i class="bi bi-building-x fs-2 text-secondary"></i>
        <p class="text-secondary mt-2">Não foi possível carregar os dados da empresa.</p>
        <a href="<?= BASE ?>index.php?page=login" class="btn btn-outline-primary btn-sm">Fazer login novamente</a>
      </div>

    <?php else: ?>

      <!-- INFO DA EMPRESA -->
      <div class="row g-4 mb-5">
        <div class="col-lg-4">
          <div class="vaga-card">
            <div class="vaga-card-top">
              <div class="empresa-logo">
                <div class="empresa-logo-placeholder" style="display:flex;">
                  <i class="bi bi-building fs-4"></i>
                </div>
              </div>
            </div>
            <h5 class="vaga-titulo"><?= htmlspecialchars($empresa->getNome()) ?></h5>
            <div class="vaga-infos">
              <span><i class="bi bi-upc-scan"></i> CNPJ: <?= $empresa->getCnpjFormatado() ?></span>
              <?php if ($empresa->getTelefone()): ?>
                <span><i class="bi bi-telephone"></i> <?= htmlspecialchars($empresa->getTelefone()) ?></span>
              <?php endif; ?>
              <?php if ($empresa->getDescricao()): ?>
                <span><i class="bi bi-info-circle"></i> <?= htmlspecialchars($empresa->getDescricao()) ?></span>
              <?php endif; ?>
            </div>
          </div>
        </div>
      </div>

      <!-- VAGAS DA EMPRESA -->
      <div class="d-flex align-items-center justify-content-between mb-3">
        <h4 class="fw-semibold mb-0">Minhas Vagas</h4>
        <a href="<?= BASE ?>index.php?page=empresa-vaga-form" class="btn btn-primary btn-sm">
          <i class="bi bi-plus-lg me-1"></i> Nova vaga
        </a>
      </div>

      <?php if (empty($vagas)): ?>
        <div class="vagas-vazio">
          <i class="bi bi-briefcase"></i>
          <p>Você ainda não cadastrou nenhuma vaga.</p>
          <a href="<?= BASE ?>index.php?page=empresa-vaga-form" class="btn btn-outline-primary btn-sm">Criar primeira vaga</a>
        </div>

      <?php else: ?>
        <div class="row g-4">
          <?php foreach ($vagas as $vaga): ?>
            <div class="col-lg-6 col-12">
              <div class="vaga-card">
                <div class="vaga-card-top">
                  <span class="<?= statusVagaBadge($vaga->getStatus()) ?>">
                    <?= statusVagaLabel($vaga->getStatus()) ?>
                  </span>
                  <span class="vaga-badge ms-auto"><?= $vaga->getModalidadeLabel() ?></span>
                </div>

                <h5 class="vaga-titulo"><?= htmlspecialchars($vaga->getTitulo()) ?></h5>

                <div class="vaga-infos">
                  <span><i class="bi bi-geo-alt"></i> <?= htmlspecialchars($vaga->getLocalizacao()) ?></span>
                  <span><i class="bi bi-laptop"></i> <?= htmlspecialchars($vaga->getArea()) ?></span>
                </div>

                <div class="vaga-footer">
                  <span class="vaga-bolsa"><?= $vaga->getSalarioFormatado() ?></span>
                  <div class="d-flex gap-2">
                    <a href="<?= BASE ?>index.php?page=empresa-candidatos&vaga_id=<?= $vaga->getId() ?>"
                       class="btn btn-outline-primary btn-sm">
                      <i class="bi bi-people"></i> Candidatos
                    </a>
                    <a href="<?= BASE ?>index.php?page=empresa-vaga-form&vaga_id=<?= $vaga->getId() ?>"
                       class="btn btn-outline-secondary btn-sm">
                      <i class="bi bi-pencil"></i> Editar
                    </a>
                  </div>
                </div>
              </div>
            </div>
          <?php endforeach; ?>
        </div>
      <?php endif; ?>

    <?php endif; ?>

  </div>
</section>
