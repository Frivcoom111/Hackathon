<?php

$isEdit = $job !== null;
?>

<section class="page">
  <div class="page-title">
    <h1><?= $isEdit ? 'Editar vaga' : 'Nova vaga' ?></h1>
    <span class="pill">Empresa</span>
  </div>
  <form class="panel form-grid" method="post" action="<?= e($action) ?>">
    <?= csrf_field() ?>
    <h2>Dados da vaga</h2>
    <label>Titulo<input type="text" name="title" value="<?= e($isEdit ? $job->title() : '') ?>" required></label>
    <label>Area<input type="text" name="area" value="<?= e($isEdit ? $job->area() : '') ?>" required></label>
    <label>Modalidade
      <select name="modality" required>
        <option value="PRESENCIAL" <?= selected($isEdit ? $job->modality() : '', 'PRESENCIAL') ?>>Presencial</option>
        <option value="HYBRID" <?= selected($isEdit ? $job->modality() : '', 'HYBRID') ?>>Hibrido</option>
        <option value="REMOTE" <?= selected($isEdit ? $job->modality() : '', 'REMOTE') ?>>Remoto</option>
      </select>
    </label>
    <label>Local<input type="text" name="location" value="<?= e($isEdit ? $job->location() : 'Umuarama, PR') ?>" required></label>
    <label>Bolsa<input type="number" name="salary" step="0.01" value="<?= e($isEdit ? $job->salary() : '') ?>"></label>
    <label>ID do curso<input type="text" name="courseId" value="<?= e($isEdit ? $job->courseId() : '') ?>"></label>
    <label>Status
      <select name="status">
        <option value="ACTIVE" <?= selected($isEdit ? $job->status() : 'ACTIVE', 'ACTIVE') ?>>Ativa</option>
        <option value="PAUSED" <?= selected($isEdit ? $job->status() : '', 'PAUSED') ?>>Pausada</option>
        <option value="CLOSED" <?= selected($isEdit ? $job->status() : '', 'CLOSED') ?>>Encerrada</option>
      </select>
    </label>
    <label class="span-all">Descricao<textarea name="description" rows="5" required><?= e($isEdit ? $job->description() : '') ?></textarea></label>
    <label class="span-all">Requisitos<textarea name="requirements" rows="4"><?= e($isEdit ? implode(PHP_EOL, $job->requirements()) : '') ?></textarea></label>
    <div class="form-actions">
      <button class="btn btn-primary" type="submit"><i data-lucide="save"></i>Salvar</button>
      <a class="btn btn-light" href="<?= e(url('/empresa/vagas')) ?>">Cancelar</a>
    </div>
  </form>
</section>
