<section class="page">
  <div class="page-title">
    <h1>Notificacoes</h1>
    <span class="pill">Aluno</span>
  </div>
  <div class="list">
    <?php foreach ($notifications as $notification): ?>
      <article class="row-card">
        <div>
          <h2><?= e($notification->title()) ?></h2>
          <p><?= e($notification->message()) ?></p>
        </div>
        <span class="tag <?= $notification->isRead() ? 'tag-muted' : 'tag-info' ?>"><?= $notification->isRead() ? 'Lida' : 'Nova' ?></span>
        <?php if (!$notification->isRead()): ?>
          <form method="post" action="<?= e(url('/aluno/notificacoes/' . $notification->id() . '/ler')) ?>">
            <?= csrf_field() ?>
            <button class="btn btn-light" type="submit"><i data-lucide="check"></i>Marcar</button>
          </form>
        <?php endif; ?>
      </article>
    <?php endforeach; ?>
  </div>
</section>
