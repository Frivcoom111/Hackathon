<div class="table-wrap">
  <table>
    <thead>
      <tr>
        <th>Vaga</th>
        <th>Empresa</th>
        <th>Status</th>
        <th>Criada em</th>
        <?php if (!empty($showActions)): ?><th>Acao</th><?php endif; ?>
      </tr>
    </thead>
    <tbody>
      <?php foreach ($applications as $application): ?>
        <tr>
          <td><?= e($application->jobTitle()) ?></td>
          <td><?= e($application->companyName()) ?></td>
          <td><span class="tag <?= e(status_class($application->status())) ?>"><?= e(status_label($application->status())) ?></span></td>
          <td><?= e($application->createdAt()) ?></td>
          <?php if (!empty($showActions)): ?>
            <td>
              <?php if ($application->status() === 'PENDING'): ?>
                <form method="post" action="<?= e(url('/aluno/candidaturas/' . $application->id() . '/cancelar')) ?>">
                  <?= csrf_field() ?>
                  <button class="btn btn-light" type="submit"><i data-lucide="x"></i>Cancelar</button>
                </form>
              <?php else: ?>
                <span class="muted">Sem acao</span>
              <?php endif; ?>
            </td>
          <?php endif; ?>
        </tr>
      <?php endforeach; ?>
    </tbody>
  </table>
</div>
