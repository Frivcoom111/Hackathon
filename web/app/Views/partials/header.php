<?php

use App\Core\Session;

$role = Session::role();
$dashboardPath = $role === 'COMPANY' ? '/empresa/dashboard' : '/aluno/dashboard';
?>

<header class="topbar">
  <a class="brand" href="<?= e(url('/')) ?>">Uni<span>ALFA</span></a>

  <nav class="nav">
    <a class="<?= e(is_active('/')) ?>" href="<?= e(url('/')) ?>">Inicio</a>
    <a class="<?= e(is_active('/vagas')) ?>" href="<?= e(url('/vagas')) ?>">Vagas</a>
    <a class="<?= e(is_active('/empresas')) ?>" href="<?= e(url('/empresas')) ?>">Empresas</a>
    <a class="<?= e(is_active('/conteudos')) ?>" href="<?= e(url('/conteudos')) ?>">Conteudos</a>
    <a class="<?= e(is_active('/sobre')) ?>" href="<?= e(url('/sobre')) ?>">Sobre</a>
    <?php if ($role === 'STUDENT'): ?>
      <a href="<?= e(url('/aluno/candidaturas')) ?>">Candidaturas</a>
      <a href="<?= e(url('/aluno/notificacoes')) ?>">Notificacoes</a>
    <?php endif; ?>
    <?php if ($role === 'COMPANY'): ?>
      <a href="<?= e(url('/empresa/vagas')) ?>">Minhas vagas</a>
    <?php endif; ?>
  </nav>

  <div class="actions">
    <?php if (Session::isAuthenticated()): ?>
      <a class="btn btn-ghost" href="<?= e(url($dashboardPath)) ?>"><i data-lucide="layout-dashboard"></i>Painel</a>
      <a class="btn btn-primary" href="<?= e(url('/logout')) ?>"><i data-lucide="log-out"></i>Sair</a>
    <?php else: ?>
      <a class="btn btn-ghost" href="<?= e(url('/login')) ?>"><i data-lucide="log-in"></i>Entrar</a>
      <a class="btn btn-primary" href="<?= e(url('/cadastro/aluno')) ?>"><i data-lucide="user-plus"></i>Cadastrar</a>
    <?php endif; ?>
  </div>
</header>
