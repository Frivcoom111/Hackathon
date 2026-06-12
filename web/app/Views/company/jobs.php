<section class="page">
  <div class="page-title">
    <h1>Minhas vagas</h1>
    <a class="btn btn-primary" href="<?= e(url('/empresa/vagas/nova')) ?>"><i data-lucide="plus"></i>Nova vaga</a>
  </div>
  <div class="table-wrap">
    <table>
      <thead><tr><th>Titulo</th><th>Area</th><th>Modalidade</th><th>Status</th><th>Acoes</th></tr></thead>
      <tbody>
        <?php foreach ($jobs as $job): ?>
          <tr>
            <td><?= e($job->title()) ?></td>
            <td><?= e($job->area()) ?></td>
            <td><?= e(modality_label($job->modality())) ?></td>
            <td><span class="tag <?= e(status_class($job->status())) ?>"><?= e(status_label($job->status())) ?></span></td>
            <td class="actions-cell">
              <a class="btn btn-light" href="<?= e(url('/empresa/vagas/' . $job->id() . '/candidatos')) ?>">Candidatos</a>
              <a class="btn btn-light" href="<?= e(url('/empresa/vagas/' . $job->id() . '/editar')) ?>"><i data-lucide="pencil"></i>Editar</a>
              <form method="post" action="<?= e(url('/empresa/vagas/' . $job->id() . '/excluir')) ?>">
                <?= csrf_field() ?>
                <button class="btn btn-danger" type="submit"><i data-lucide="trash-2"></i>Excluir</button>
              </form>
            </td>
          </tr>
        <?php endforeach; ?>
      </tbody>
    </table>
  </div>
</section>
