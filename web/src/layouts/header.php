<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><?= isset($titulo_pagina) ? $titulo_pagina . ' | Hackathon' : 'Hackathon' ?></title>

  <!-- Bootstrap CSS (Local) -->
  <link href="<?= BASE ?>assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

  <!-- Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">

  <!-- Google Fonts -->
  <link href="https://fonts.googleapis.com/css2?family=Source+Sans+3:wght@400;600;700&family=Inter:wght@400;500&display=swap" rel="stylesheet">

  <!-- CSS do projeto -->
  <link rel="stylesheet" href="<?= BASE ?>css/style.css">
</head>
<body>

<?php
$logado = !empty($_SESSION['token']);
$role   = $_SESSION['role'] ?? '';
?>

<nav class="navbar navbar-expand-lg sticky-top">
  <div class="container navbar-container">

    <!-- Logo -->
    <a class="navbar-brand fw-bold" href="<?= BASE ?>index.php">
      <img src="<?= BASE ?>assets/images/site/logo.png" alt="Hackathon">
    </a>

    <!-- Botão hamburguer (mobile) -->
    <button class="navbar-toggler border-0" type="button"
            data-bs-toggle="collapse" data-bs-target="#navbarMenu"
            aria-controls="navbarMenu" aria-expanded="false" aria-label="Abrir menu">
      <i class="bi bi-list text-white fs-3"></i>
    </button>

    <!-- Conteúdo colapsável -->
    <div class="collapse navbar-collapse" id="navbarMenu">

      <!-- Links centralizados — "Alunos" foi removido pois é exclusivo do admin (back office Java) -->
      <ul class="navbar-nav mx-auto align-items-center gap-lg-1 text-center">
        <li class="nav-item"><a class="nav-link" href="<?= BASE ?>index.php?page=home">Início</a></li>
        <li class="nav-item"><a class="nav-link" href="<?= BASE ?>index.php?page=empresas">Empresas</a></li>
        <li class="nav-item"><a class="nav-link" href="<?= BASE ?>index.php?page=vagas">Vagas</a></li>
      </ul>

      <?php if (!$logado): ?>
      <div class="d-flex align-items-center justify-content-center gap-2 mt-3 mt-lg-0">
        <a href="<?= BASE ?>index.php?page=login" class="btn btn-entrar btn-sm px-3">Entrar</a>
        <a href="<?= BASE ?>index.php?page=cadastro" class="btn btn-warning btn-sm px-3 fw-semibold">Cadastrar</a>
      </div>
      <?php endif; ?>

      <?php if ($logado): ?>
      <div class="d-flex align-items-center justify-content-center gap-2 mt-3 mt-lg-0">
        <?php if ($role === 'empresa'): ?>
          <a href="<?= BASE ?>index.php?page=empresa-dashboard" class="btn btn-entrar btn-sm px-3">
            <i class="bi bi-grid me-1"></i> Painel
          </a>
        <?php else: ?>
          <a href="<?= BASE ?>index.php?page=perfil" class="btn btn-entrar btn-sm px-3">
            <i class="bi bi-person-circle me-1"></i> Meu perfil
          </a>
        <?php endif; ?>
        <a href="<?= BASE ?>index.php?page=logout" class="btn btn-outline-light btn-sm px-3">Sair</a>
      </div>
      <?php endif; ?>

    </div>
  </div>
</nav>