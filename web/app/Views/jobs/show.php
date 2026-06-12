<section class="page">
  <div class="page-title">
    <h1>Detalhe da vaga</h1>
    <span class="pill">Publico</span>
  </div>

  <div class="detail-layout">
    <article class="panel">
      <h2><?= e($job->title()) ?></h2>
      <p class="muted"><?= e($job->companyName()) ?> - <?= e($job->location()) ?></p>
      <div class="tag-row">
        <span class="tag tag-info"><?= e(modality_label($job->modality())) ?></span>
        <span class="tag tag-warning"><?= e(money($job->salary())) ?></span>
        <span class="tag tag-success"><?= e($job->area()) ?></span>
      </div>
      <h3>Descricao</h3>
      <p><?= e($job->description()) ?></p>
      <h3>Requisitos</h3>
      <ul class="clean-list">
        <?php foreach ($job->requirements() as $requirement): ?>
          <li><?= e($requirement) ?></li>
        <?php endforeach; ?>
      </ul>
    </article>

    <aside class="compat">
      <h2>Candidatura</h2>
      <span class="tag tag-success">Aluno apto</span>
      <span class="tag tag-success">Vaga ativa</span>
      <form method="post" action="<?= e(url('/vagas/' . $job->id() . '/candidatar')) ?>">
        <?= csrf_field() ?>
        <label>Mensagem opcional<textarea name="coverLetter" rows="5" placeholder="Conte brevemente por que esta vaga combina com voce"></textarea></label>
        <button class="btn btn-primary full" type="submit"><i data-lucide="send"></i>Candidatar-se</button>
      </form>
    </aside>
  </div>

  <section class="page-section">
    <div class="section-title"><h2>Vagas relacionadas</h2></div>
    <div class="card-grid two">
      <?php foreach ($related as $job): ?>
        <?php require BASE_PATH . '/app/Views/partials/job-card.php'; ?>
      <?php endforeach; ?>
    </div>
  </section>
</section>
