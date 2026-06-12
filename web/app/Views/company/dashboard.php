<?php

$analysing = count(array_filter($applications, static fn ($item): bool => $item->status() === 'ANALYSING'));
$approved = count(array_filter($applications, static fn ($item): bool => $item->status() === 'APPROVED'));
?>

<section class="page">
  <div class="dashboard-hero">
    <div>
      <span class="eyebrow">Painel da empresa</span>
      <h1>Gestao de vagas e candidatos</h1>
      <p>Publique oportunidades e acompanhe alunos interessados.</p>
    </div>
    <a class="btn btn-primary" href="<?= e(url('/empresa/vagas/nova')) ?>"><i data-lucide="plus"></i>Nova vaga</a>
  </div>

  <div class="stats">
    <article><span>Vagas ativas</span><strong><?= count($jobs) ?></strong></article>
    <article><span>Candidatos</span><strong><?= count($applications) ?></strong></article>
    <article><span>Em analise</span><strong><?= $analysing ?></strong></article>
    <article><span>Aprovados</span><strong><?= $approved ?></strong></article>
  </div>

  <article class="panel">
    <div class="section-title">
      <h2>Ultimos candidatos</h2>
      <a class="btn btn-light" href="<?= e(url('/empresa/vagas')) ?>">Gerenciar vagas</a>
    </div>
    <div class="table-wrap">
      <table>
        <thead><tr><th>Aluno</th><th>Vaga</th><th>Status</th><th>Acao</th></tr></thead>
        <tbody>
          <?php foreach ($applications as $application): ?>
            <tr>
              <td><?= e($application->studentName()) ?></td>
              <td><?= e($application->jobTitle()) ?></td>
              <td><span class="tag <?= e(status_class($application->status())) ?>"><?= e(status_label($application->status())) ?></span></td>
              <td>
                <form class="inline-form" method="post" action="<?= e(url('/empresa/candidaturas/' . $application->id() . '/status')) ?>">
                  <?= csrf_field() ?>
                  <input type="hidden" name="jobId" value="<?= e($application->jobId()) ?>">
                  <input type="hidden" name="returnTo" value="/empresa/dashboard">
                  <select name="status">
                    <option value="ANALYSING">Analise</option>
                    <option value="APPROVED">Aprovar</option>
                    <option value="REJECTED">Reprovar</option>
                  </select>
                  <button class="btn btn-primary" type="submit">Salvar</button>
                </form>
              </td>
            </tr>
          <?php endforeach; ?>
        </tbody>
      </table>
    </div>
  </article>
</section>
