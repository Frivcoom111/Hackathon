<?php
require_once __DIR__ . '/../../classes/Vaga.php';

\App\Auth\Guard::requireLogin($api->jwt());

$resp  = $api->vagas()->listar(['limit' => 3]);
$vagas = array_map(fn($item) => new Vaga($item), $resp['data'] ?? []);
?>

<!-- CARROSSEL -->
<section class="banner-section">
  <div class="container">
    <div class="banner-box">

      <div id="bannerCarousel" class="carousel slide h-100" data-bs-ride="carousel" data-bs-interval="5000">

        <!-- Indicadores -->
        <div class="carousel-indicators">
          <button type="button" data-bs-target="#bannerCarousel" data-bs-slide-to="0" class="active" aria-current="true" aria-label="Slide 1"></button>
          <button type="button" data-bs-target="#bannerCarousel" data-bs-slide-to="1" aria-label="Slide 2"></button>
          <button type="button" data-bs-target="#bannerCarousel" data-bs-slide-to="2" aria-label="Slide 3"></button>
        </div>

        <!-- Slides -->
        <div class="carousel-inner h-100">

          <div class="carousel-item active h-100">
            <img src="<?= BASE ?>assets/images/site/faculdade.jpg" class="d-block w-100 h-100 banner-slide-img" alt="Portal de Estágios UniALFA">
          </div>

          <div class="carousel-item h-100">
            <img src="<?= BASE ?>assets/images/site/profs.jpg" class="d-block w-100 h-100 banner-slide-img" alt="Seu Talento Abre Caminhos">
          </div>

          <div class="carousel-item h-100">
            <img src="<?= BASE ?>assets/images/site/devs.jpg" class="d-block w-100 h-100 banner-slide-img" alt="Equipe UniALFA">
          </div>

        </div>

        <!-- Controles anterior / próximo -->
        <button class="carousel-control-prev" type="button" data-bs-target="#bannerCarousel" data-bs-slide="prev">
          <span class="carousel-control-prev-icon" aria-hidden="true"></span>
          <span class="visually-hidden">Anterior</span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#bannerCarousel" data-bs-slide="next">
          <span class="carousel-control-next-icon" aria-hidden="true"></span>
          <span class="visually-hidden">Próximo</span>
        </button>

      </div><!-- /#bannerCarousel -->

    </div><!-- /.banner-box -->
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
