<section class="page">
  <div class="page-title">
    <h1>Candidatos</h1>
    <span class="pill"><?= e($job?->title() ?? 'Vaga') ?></span>
  </div>
  <div class="table-wrap">
    <table>
      <thead><tr><th>Aluno</th><th>Status</th><th>Carta</th><th>Acao</th></tr></thead>
      <tbody>
        <?php foreach ($applications as $application): ?>
          <tr>
            <td><?= e($application->studentName()) ?></td>
            <td><span class="tag <?= e(status_class($application->status())) ?>"><?= e(status_label($application->status())) ?></span></td>
            <td><?= e($application->coverLetter() ?? '-') ?></td>
            <td>
              <form class="inline-form" method="post" action="<?= e(url('/empresa/candidaturas/' . $application->id() . '/status')) ?>">
                <?= csrf_field() ?>
                <input type="hidden" name="jobId" value="<?= e($job?->id() ?? $application->jobId()) ?>">
                <input type="hidden" name="returnTo" value="<?= e('/empresa/vagas/' . ($job?->id() ?? $application->jobId()) . '/candidatos') ?>">
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
</section>
