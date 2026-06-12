<?php

use App\Core\Session;

$success = $success ?? Session::pullFlash('success');
$error = $error ?? Session::pullFlash('error');
?>

<?php if (!empty($success)): ?>
  <p class="alert alert-success"><?= e($success) ?></p>
<?php endif; ?>

<?php if (!empty($error)): ?>
  <p class="alert alert-error"><?= e($error) ?></p>
<?php endif; ?>
