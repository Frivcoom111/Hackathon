<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><?= e($title ?? app_config('name')) ?></title>
  <link rel="stylesheet" href="<?= e(asset('css/app.css') . '?v=' . filemtime(BASE_PATH . '/public/assets/css/app.css')) ?>">
  <script src="https://unpkg.com/lucide@latest" defer></script>
  <script defer>
    document.addEventListener('DOMContentLoaded', function () {
      if (window.lucide) window.lucide.createIcons();
    });
  </script>
</head>
<?php
$bodyClass = trim((string) ($bodyClass ?? ''));
$hideChrome = (bool) ($hideChrome ?? false);
$mainClass = $hideChrome ? 'auth-main' : 'shell';
?>
<body class="<?= e($bodyClass) ?>">
  <?php if (!$hideChrome): ?>
    <?php require BASE_PATH . '/app/Views/partials/header.php'; ?>
  <?php endif; ?>
  <main class="<?= e($mainClass) ?>">
    <?php require BASE_PATH . '/app/Views/partials/flash.php'; ?>
    <?= $content ?>
  </main>
  <?php if (!$hideChrome): ?>
    <?php require BASE_PATH . '/app/Views/partials/footer.php'; ?>
  <?php endif; ?>
</body>
</html>
