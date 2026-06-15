<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><?= isset($titulo_pagina) ? $titulo_pagina . ' | Hackathon' : 'Hackathon' ?></title>

  <link href="<?= BASE ?>assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Source+Sans+3:wght@400;600;700&family=Inter:wght@400;500&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="<?= BASE ?>css/style.css">
</head>
<body>

<?php
$logado = !empty($_SESSION['token']);
$role = $_SESSION['role'] ?? '';
$semNavbar = in_array($pagina ?? '', ['login', 'cadastro'], true);
?>

<?php if (!$semNavbar): ?>
<nav class="navbar navbar-expand-lg sticky-top">
  <div class="container navbar-container">
    <a class="navbar-brand fw-bold" href="<?= BASE ?>index.php?page=home">
      <img src="<?= BASE ?>assets/images/site/logo.png" alt="UniALFA Portal de Estagio">
    </a>

    <button class="navbar-toggler border-0" type="button"
            data-bs-toggle="collapse" data-bs-target="#navbarMenu"
            aria-controls="navbarMenu" aria-expanded="false" aria-label="Abrir menu">
      <i class="bi bi-list text-white fs-3"></i>
    </button>

    <div class="collapse navbar-collapse" id="navbarMenu">
      <ul class="navbar-nav mx-auto align-items-center gap-lg-1 text-center">
        <?php if ($logado && $role === 'empresa'): ?>
          <li class="nav-item"><a class="nav-link" href="<?= BASE ?>index.php?page=empresa-dashboard">Painel</a></li>
          <li class="nav-item"><a class="nav-link" href="<?= BASE ?>index.php?page=empresa-dashboard#empresa-vagas">Minhas vagas</a></li>
          <li class="nav-item"><a class="nav-link" href="<?= BASE ?>index.php?page=empresa-vaga-form">Nova vaga</a></li>
          <li class="nav-item"><a class="nav-link" href="<?= BASE ?>index.php?page=empresa-candidatos">Candidatos</a></li>
        <?php else: ?>
          <li class="nav-item"><a class="nav-link" href="<?= BASE ?>index.php?page=home">Inicio</a></li>
          <li class="nav-item"><a class="nav-link" href="<?= BASE ?>index.php?page=empresas">Empresas</a></li>
          <li class="nav-item"><a class="nav-link" href="<?= BASE ?>index.php?page=vagas">Vagas</a></li>
        <?php endif; ?>
      </ul>

      <?php if (!$logado): ?>
        <div class="d-flex align-items-center justify-content-center gap-2 mt-3 mt-lg-0">
          <a href="<?= BASE ?>index.php?page=login" class="btn btn-entrar btn-sm px-3">Entrar</a>
          <a href="<?= BASE ?>index.php?page=cadastro" class="btn btn-warning btn-sm px-3 fw-semibold">Cadastrar</a>
        </div>
      <?php else: ?>
        <div class="d-flex align-items-center justify-content-center gap-2 mt-3 mt-lg-0">
          <?php if ($role === 'empresa'): ?>
            <a href="<?= BASE ?>index.php?page=empresa-dashboard" class="btn btn-entrar btn-sm px-3">
              <i class="bi bi-building me-1"></i> Minha empresa
            </a>
          <?php else: ?>
            <a href="<?= BASE ?>index.php?page=perfil" class="btn btn-entrar btn-sm px-3">
              <i class="bi bi-person-circle me-1"></i> Eu
            </a>
          <?php endif; ?>
          <a href="<?= BASE ?>index.php?page=logout" class="btn btn-outline-light btn-sm px-3">Sair</a>
        </div>
      <?php endif; ?>
    </div>
  </div>
</nav>
<?php endif; ?>
