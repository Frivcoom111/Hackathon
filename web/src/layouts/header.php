<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><?= isset($titulo_pagina) ? $titulo_pagina . ' | Hackathon' : 'Hackathon' ?></title>

  <!-- Bootstrap CSS (Local) -->
  <link href="assets/vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

  <!-- Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css" rel="stylesheet">

  <!-- Google Fonts -->
  <link href="https://fonts.googleapis.com/css2?family=Source+Sans+3:wght@400;600;700&family=Inter:wght@400;500&display=swap" rel="stylesheet">

  <!-- CSS do projeto -->
  <link rel="stylesheet" href="css/style.css">
</head>
<body>

<?php
// Simulação de sessão — trocar pela sua lógica real depois
// session_start();
// $logado = isset($_SESSION['usuario']);
$logado = false; // trocar por $logado = isset($_SESSION['usuario']);
?>

<nav class="navbar navbar-expand-lg sticky-top">
  <div class="container navbar-container">

    <!-- Logo -->
    <a class="navbar-brand fw-bold" href="/index.php">
      <img src="assets/images/site/logo.png" alt="Hackathon">
    </a>

    <!-- Botão hamburguer (mobile) -->
    <button class="navbar-toggler border-0" type="button"
            data-bs-toggle="collapse" data-bs-target="#navbarMenu"
            aria-controls="navbarMenu" aria-expanded="false" aria-label="Abrir menu">
      <i class="bi bi-list text-white fs-3"></i>
    </button>

    <!-- Conteúdo colapsável -->
    <div class="collapse navbar-collapse" id="navbarMenu">

      <!-- Links centralizados -->
      <ul class="navbar-nav mx-auto align-items-center gap-lg-1 text-center">
        <li class="nav-item">
          <a class="nav-link" href="/index.php">Início</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="/pages/empresas.php">Empresas</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="/pages/vagas.php">Vagas</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" href="/pages/alunos.php">Alunos</a>
        </li>
      </ul>

      <!-- Botões de auth (só aparece se não estiver logado) -->
      <?php if (!$logado): ?>
      <div class="d-flex align-items-center justify-content-center gap-2 mt-3 mt-lg-0">
        <a href="/pages/login.php" class="btn btn-entrar btn-sm px-3">Entrar</a>
        <a href="/pages/cadastro.php" class="btn btn-warning btn-sm px-3 fw-semibold">Cadastrar</a>
      </div>
      <?php endif; ?>

      <!-- Se estiver logado, mostra perfil -->
      <?php if ($logado): ?>
      <div class="d-flex align-items-center justify-content-center gap-2 mt-3 mt-lg-0">
        <a href="/pages/perfil.php" class="btn btn-entrar btn-sm px-3">
          <i class="bi bi-person-circle me-1"></i> Meu perfil
        </a>
      </div>
      <?php endif; ?>

    </div>
  </div>
</nav>