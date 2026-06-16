<?php
require_once __DIR__ . '/../classes/Notificacao.php';

\App\Auth\Guard::requireLogin($api->jwt());

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $acao = $_POST['acao'] ?? '';

    if ($acao === 'marcar_todas') {
        $api->notificacoes()->marcarTodasLidas();
    } elseif ($acao === 'marcar_lida' && !empty($_POST['id'])) {
        $api->notificacoes()->marcarLida(trim($_POST['id']));
    }

    $voltar = $_GET['filtro'] ?? '';
    header('Location: ' . BASE . 'index.php?page=notificacoes' . ($voltar ? '&filtro=' . urlencode($voltar) : ''));
    exit;
}

// Filtro: 'nao-lidas' | qualquer outro = todas
$filtro = $_GET['filtro'] ?? 'todas';
$unread = $filtro === 'nao-lidas' ? true : null;

$resp = $api->notificacoes()->listar($unread, 1, 50);
$notificacoes = [];
foreach ($resp['data'] ?? [] as $n) {
    $notificacoes[] = new Notificacao($n);
}
$total = $resp['meta']['total'] ?? count($notificacoes);
?>

<section class="vagas-hero">
  <div class="container">
    <h1 class="vagas-hero-titulo">Notificações</h1>
    <p class="vagas-hero-sub">Acompanhe o andamento das suas candidaturas</p>
  </div>
</section>

<section class="notif-page">
  <div class="container py-4">

    <div class="notif-toolbar">
      <div class="notif-filtros" role="tablist">
        <a href="<?= BASE ?>index.php?page=notificacoes"
           class="notif-filtro <?= $filtro !== 'nao-lidas' ? 'active' : '' ?>">Todas</a>
        <a href="<?= BASE ?>index.php?page=notificacoes&filtro=nao-lidas"
           class="notif-filtro <?= $filtro === 'nao-lidas' ? 'active' : '' ?>">Não lidas</a>
      </div>

      <?php if (!empty($notificacoes)): ?>
        <form method="POST" action="<?= BASE ?>index.php?page=notificacoes&filtro=<?= htmlspecialchars($filtro) ?>" class="m-0">
          <input type="hidden" name="acao" value="marcar_todas">
          <button type="submit" class="btn btn-outline-primary btn-sm">
            <i class="bi bi-check2-all me-1"></i> Marcar todas como lidas
          </button>
        </form>
      <?php endif; ?>
    </div>

    <?php if (empty($notificacoes)): ?>
      <div class="vagas-vazio">
        <i class="bi bi-bell-slash"></i>
        <p><?= $filtro === 'nao-lidas' ? 'Nenhuma notificação não lida.' : 'Você ainda não tem notificações.' ?></p>
      </div>
    <?php else: ?>
      <ul class="notif-lista">
        <?php foreach ($notificacoes as $n): ?>
          <li class="notif-linha <?= $n->isLida() ? '' : 'notif-linha--nova' ?>">
            <span class="notif-linha-icone"><i class="bi <?= $n->getIconeClasse() ?>"></i></span>
            <div class="notif-linha-texto">
              <strong><?= htmlspecialchars($n->getTitulo()) ?></strong>
              <p><?= htmlspecialchars($n->getMensagem()) ?></p>
              <small><?= htmlspecialchars($n->tempoRelativo()) ?></small>
            </div>
            <?php if (!$n->isLida()): ?>
              <form method="POST" action="<?= BASE ?>index.php?page=notificacoes&filtro=<?= htmlspecialchars($filtro) ?>" class="m-0">
                <input type="hidden" name="acao" value="marcar_lida">
                <input type="hidden" name="id" value="<?= htmlspecialchars($n->getId()) ?>">
                <button type="submit" class="btn btn-link btn-sm notif-marcar-uma" aria-label="Marcar como lida">
                  <i class="bi bi-check2"></i>
                </button>
              </form>
            <?php endif; ?>
          </li>
        <?php endforeach; ?>
      </ul>
    <?php endif; ?>

  </div>
</section>
