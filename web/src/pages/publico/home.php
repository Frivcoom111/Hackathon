<?php
require_once __DIR__ . '/../../classes/Vaga.php';

\App\Auth\Guard::requireLogin($api->jwt());

$resp  = $api->vagas()->listar(['limit' => 3]);
$vagas = array_map(fn($item) => new Vaga($item), $resp['data'] ?? []);
?>

<!-- BANNER -->
<section class="banner-section">
  <div class="container">
    <div class="banner-box">
      <img src="<?= BASE ?>assets/images/site/login.png" alt="Portal de Estagios UniALFA"
           onerror="this.style.display='none'; this.nextElementSibling.style.display='flex'">
      <div class="banner-placeholder" style="display:none;">
        <span>Banner</span>
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
        <p class="vagas-sub">Oportunidades selecionadas para você</p>
      </div>
      <a href="<?= BASE ?>index.php?page=vagas" class="btn btn-outline-primary btn-sm">Ver todas</a>
    </div>

    <div class="row g-4">

      <?php if (empty($vagas)): ?>
        <div class="col-12 text-center py-4">
          <i class="bi bi-briefcase fs-2 text-secondary"></i>
          <p class="text-secondary mt-2">Nenhuma vaga disponível no momento.</p>
        </div>

      <?php else: ?>
        <?php foreach ($vagas as $vaga): ?>
          <div class="col-lg-4 col-md-6">
            <div class="vaga-card">
              <div class="vaga-card-top">
                <div class="empresa-logo">
                  <div class="empresa-logo-placeholder" style="display:flex;">
                    <i class="bi bi-building"></i>
                  </div>
                </div>
                <span class="vaga-badge">Estágio</span>
              </div>
              <h5 class="vaga-titulo"><?= htmlspecialchars($vaga->getTitulo()) ?></h5>
              <p class="vaga-empresa"><?= htmlspecialchars($vaga->getArea()) ?></p>
              <div class="vaga-infos">
                <span><i class="bi bi-geo-alt"></i> <?= htmlspecialchars($vaga->getLocalizacao()) ?></span>
                <span><i class="bi bi-building"></i> <?= htmlspecialchars($vaga->getModalidadeLabel()) ?></span>
              </div>
              <div class="vaga-footer">
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
