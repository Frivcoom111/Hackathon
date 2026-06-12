<?php
$firstJob = $jobs[0] ?? null;
$firstJobPath = $firstJob ? '/vagas/' . $firstJob->id() : '/vagas';
?>

<section class="hero">
  <div class="hero-copy">
    <span class="eyebrow">Portal de Estagios UniALFA</span>
    <h1>Conectando alunos a oportunidades reais na regiao.</h1>
    <p>Vagas, candidaturas e comunicacao de status em um fluxo simples para aluno e empresa.</p>
    <div class="hero-actions">
      <a class="btn btn-primary" href="<?= e(url('/vagas')) ?>"><i data-lucide="search"></i>Buscar vagas</a>
      <a class="btn btn-hero" href="<?= e(url('/empresas')) ?>"><i data-lucide="building-2"></i>Sou empresa</a>
    </div>
  </div>
  <div class="hero-media portal-poster" role="img" aria-label="Arte institucional do Portal de Estagios UniALFA"></div>
</section>

<section class="page-section featured-jobs-section">
  <div class="section-title">
    <div>
      <h2>Vagas em destaque</h2>
      <p class="muted">Oportunidades recentes para comecar a sua jornada profissional.</p>
    </div>
    <a class="btn btn-light" href="<?= e(url('/vagas')) ?>"><i data-lucide="list-filter"></i>Ver todas</a>
  </div>
  <div class="card-grid">
    <?php foreach ($jobs as $job): ?>
      <?php require BASE_PATH . '/app/Views/partials/job-card.php'; ?>
    <?php endforeach; ?>
  </div>
</section>

<section class="page-section portal-workspace">
  <article class="panel student-path-panel">
    <div class="panel-heading">
      <div>
        <span class="eyebrow eyebrow-blue">Menu do aluno</span>
        <h2>Caminhos rapidos para sua jornada</h2>
      </div>
      <a class="btn btn-light" href="<?= e(url('/cadastro/aluno')) ?>"><i data-lucide="user-plus"></i>Criar conta</a>
    </div>
    <div class="path-menu">
      <a class="path-card" href="<?= e(url('/vagas')) ?>">
        <span class="path-icon"><i data-lucide="search"></i></span>
        <strong>Buscar vaga</strong>
        <p>Filtre oportunidades por area, local e modalidade.</p>
        <i data-lucide="arrow-right"></i>
      </a>
      <a class="path-card" href="<?= e(url($firstJobPath)) ?>">
        <span class="path-icon"><i data-lucide="file-search"></i></span>
        <strong>Ver detalhes</strong>
        <p>Confira requisitos, bolsa e informacoes da empresa.</p>
        <i data-lucide="arrow-right"></i>
      </a>
      <a class="path-card" href="<?= e(url('/login')) ?>">
        <span class="path-icon"><i data-lucide="send"></i></span>
        <strong>Candidatar-se</strong>
        <p>Entre na sua conta para enviar candidaturas.</p>
        <i data-lucide="arrow-right"></i>
      </a>
      <a class="path-card" href="<?= e(url('/aluno/candidaturas')) ?>">
        <span class="path-icon"><i data-lucide="activity"></i></span>
        <strong>Acompanhar status</strong>
        <p>Veja candidaturas pendentes, em analise e aprovadas.</p>
        <i data-lucide="arrow-right"></i>
      </a>
    </div>
  </article>

  <article class="panel company-showcase-panel">
    <div class="panel-heading">
      <div>
        <span class="eyebrow eyebrow-blue">Rede parceira</span>
        <h2>Empresas em destaque</h2>
      </div>
      <a class="btn btn-light" href="<?= e(url('/empresas')) ?>"><i data-lucide="building-2"></i>Ver empresas</a>
    </div>
    <div class="company-showcase-grid">
      <?php
      $companyImages = [
          'company-tech-local' => 'tech-local.svg',
          'company-agencia-alfa' => 'agencia-alfa.svg',
          'company-winfo' => 'winfo.svg',
      ];
      ?>
      <?php foreach ($companies as $company): ?>
        <?php $companyImage = $companyImages[$company->id()] ?? 'company-default.svg'; ?>
        <a class="company-card" href="<?= e(url('/empresas')) ?>">
          <img src="<?= e(asset('img/companies/' . $companyImage)) ?>" alt="<?= e($company->name()) ?>">
          <span><?= e($company->name()) ?></span>
          <small><?= e($company->city() ?? 'Umuarama') ?></small>
        </a>
      <?php endforeach; ?>
    </div>
  </article>
</section>
