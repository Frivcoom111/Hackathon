<section class="page">
  <div class="page-title">
    <h1>Listagem de vagas</h1>
    <span class="pill">Publico</span>
  </div>

  <form class="filters" method="get" action="<?= e(url('/vagas')) ?>">
    <label>Buscar<input type="text" name="search" value="<?= e($filters['search'] ?? '') ?>" placeholder="Cargo, empresa ou area"></label>
    <label>Area<input type="text" name="area" value="<?= e($filters['area'] ?? '') ?>" placeholder="Tecnologia"></label>
    <label>Modalidade
      <select name="modality">
        <option value="">Todas</option>
        <option value="PRESENCIAL" <?= selected($filters['modality'] ?? '', 'PRESENCIAL') ?>>Presencial</option>
        <option value="HYBRID" <?= selected($filters['modality'] ?? '', 'HYBRID') ?>>Hibrido</option>
        <option value="REMOTE" <?= selected($filters['modality'] ?? '', 'REMOTE') ?>>Remoto</option>
      </select>
    </label>
    <button class="btn btn-primary" type="submit"><i data-lucide="filter"></i>Filtrar</button>
  </form>

  <div class="list">
    <?php foreach ($jobs as $job): ?>
      <article class="row-card">
        <div>
          <h2><?= e($job->title()) ?></h2>
          <p><?= e($job->companyName()) ?> - <?= e($job->area()) ?> - <?= e($job->location()) ?></p>
        </div>
        <span class="tag tag-info"><?= e(modality_label($job->modality())) ?></span>
        <span class="tag tag-warning"><?= e(money($job->salary())) ?></span>
        <a class="btn btn-light" href="<?= e(url('/vagas/' . $job->id())) ?>">Ver</a>
      </article>
    <?php endforeach; ?>
  </div>
</section>
