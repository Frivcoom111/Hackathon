<?php
// Carrega a classe Vaga para representar cada vaga retornada pela API
require_once __DIR__ . '/../../classes/Vaga.php';
require_once __DIR__ . '/../../api.php';

// Chama a API via cURL para buscar as 3 Ãºltimas vagas ativas
$vagas = [];
$jobs = api_items(api_get('/jobs?limit=3&status=ACTIVE'), 'jobs');
if ($jobs === []) {
    $jobs = array_slice(demo_jobs(), 0, 3);
}

foreach ($jobs as $item) {
    $vagas[] = new Vaga($item);
}
?>

<section class="banner-section">
  <div class="container">
    <div class="banner-box">
      <div class="banner-copy">
        <span class="banner-kicker">Portal de Estagios UniALFA</span>
        <h1>Conectando alunos a oportunidades reais na regiao.</h1>
        <p>Vagas, empresas parceiras e candidaturas em um fluxo simples para alunos e recrutadores.</p>
        <div class="banner-actions">
          <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-warning fw-semibold">
            <i class="bi bi-search me-1"></i> Buscar vagas
          </a>
          <a href="<?= BASE ?>index.php?page=empresas" class="btn btn-outline-light">
            <i class="bi bi-building me-1"></i> Empresas
          </a>
        </div>
      </div>

      <div class="banner-media">
        <img src="<?= BASE ?>assets/images/site/login.png" alt="Portal de Estagios UniALFA">
      </div>
    </div>
  </div>
</section>

<!-- VAGAS EM DESTAQUE -->
<section class="vagas-section">
  <div class="container">

    <div class="vagas-header">
      <div>
        <h2 class="vagas-titulo">Vagas em destaque</h2>
        <p class="vagas-sub">Oportunidades selecionadas para vocÃª</p>
      </div>
      <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-outline-primary btn-sm">Ver todas</a>
    </div>

    <div class="row g-4">

      <?php if (empty($vagas)): ?>
        <!-- Sem vagas: API offline ou nenhuma vaga ativa -->
        <div class="col-12 text-center py-4">
          <i class="bi bi-briefcase fs-2 text-secondary"></i>
          <p class="text-secondary mt-2">Nenhuma vaga disponÃ­vel no momento.</p>
        </div>

      <?php else: ?>
        <!-- Renderiza um card para cada vaga recebida da API -->
        <?php foreach ($vagas as $vaga): ?>
          <div class="col-lg-4 col-md-6">
            <div class="vaga-card">
              <div class="vaga-card-top">
                <div class="empresa-logo">
                  <div class="empresa-logo-placeholder" style="display:flex;">
                    <i class="bi bi-building"></i>
                  </div>
                </div>
                <span class="vaga-badge">EstÃ¡gio</span>
              </div>
              <h5 class="vaga-titulo"><?= htmlspecialchars($vaga->getTitulo()) ?></h5>
              <p class="vaga-empresa"><?= htmlspecialchars($vaga->getArea()) ?></p>
              <div class="vaga-infos">
                <span><i class="bi bi-geo-alt"></i> <?= htmlspecialchars($vaga->getLocalizacao()) ?></span>
                <span><i class="bi bi-building"></i> <?= htmlspecialchars($vaga->getModalidadeLabel()) ?></span>
              </div>
              <div class="vaga-footer">
                <!-- getSalarioFormatado() retorna "R$ 1.200,00" ou "A combinar" -->
                <span class="vaga-bolsa"><?= $vaga->getSalarioFormatado() ?></span>
                <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-primary btn-sm">Ver vaga</a>
              </div>
            </div>
          </div>
        <?php endforeach; ?>
      <?php endif; ?>

    </div>
  </div>
</section>

