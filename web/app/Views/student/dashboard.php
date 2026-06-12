<?php

use App\Core\Session;

$user = Session::user();
$pending = count(array_filter($applications, static fn ($item): bool => $item->status() === 'PENDING'));
$approved = count(array_filter($applications, static fn ($item): bool => $item->status() === 'APPROVED'));
$unread = count(array_filter($notifications, static fn ($item): bool => !$item->isRead()));
?>

<section class="page">
  <div class="dashboard-hero">
    <div>
      <span class="eyebrow">Portal do aluno</span>
      <h1>Ola, <?= e($user['name'] ?? 'aluno') ?></h1>
      <p>Acompanhe candidaturas, notificacoes e novas oportunidades.</p>
    </div>
    <a class="btn btn-primary" href="<?= e(url('/vagas')) ?>"><i data-lucide="search"></i>Buscar vagas</a>
  </div>

  <div class="stats">
    <article><span>Candidaturas</span><strong><?= count($applications) ?></strong></article>
    <article><span>Pendentes</span><strong><?= $pending ?></strong></article>
    <article><span>Aprovadas</span><strong><?= $approved ?></strong></article>
    <article><span>Avisos novos</span><strong><?= $unread ?></strong></article>
  </div>

  <section class="page-section">
    <div class="section-title">
      <h2>Minhas candidaturas</h2>
      <a class="btn btn-light" href="<?= e(url('/aluno/candidaturas')) ?>">Ver todas</a>
    </div>
    <?php $showActions = false; require BASE_PATH . '/app/Views/partials/application-table.php'; ?>
  </section>

  <section class="page-section">
    <div class="section-title">
      <h2>Vagas recomendadas</h2>
      <a class="btn btn-light" href="<?= e(url('/vagas')) ?>">Explorar</a>
    </div>
    <div class="card-grid">
      <?php foreach ($jobs as $job): ?>
        <?php require BASE_PATH . '/app/Views/partials/job-card.php'; ?>
      <?php endforeach; ?>
    </div>
  </section>
</section>
