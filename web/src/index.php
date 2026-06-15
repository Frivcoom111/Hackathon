<?php
// Inicia a sessao para guardar o login do usuario.
session_start();
ob_start();

// Como o index.php fica na raiz, a base dos links fica vazia.
define('BASE', '');

// Rotas simples do portal.
$rotas = [
    'home'               => 'pages/publico/home.php',
    'cadastro'           => 'pages/auth/cadastro.php',
    'login'              => 'pages/auth/login.php',
    'vagas'              => 'pages/publico/vagas.php',
    'empresas'           => 'pages/publico/empresas.php',
    'perfil'             => 'pages/aluno/perfil.php',
    'empresa-dashboard'  => 'pages/empresa/dashboard.php',
    'empresa-vaga-form'  => 'pages/empresa/vaga-form.php',
    'empresa-candidatos' => 'pages/empresa/candidatos.php',
];

// Titulo da aba do navegador.
$titulos = [
    'home'              => 'Inicio',
    'cadastro'          => 'Cadastro',
    'login'             => 'Login',
    'vagas'             => 'Vagas',
    'empresas'          => 'Empresas',
    'perfil'            => 'Meu Perfil',
    'empresa-dashboard' => 'Painel da Empresa',
    'empresa-vaga-form' => 'Vaga da Empresa',
    'empresa-candidatos' => 'Candidatos',
    '404'               => 'Pagina nao encontrada',
];

// Se nao vier ?page=, abre a home.
$pagina = $_GET['page'] ?? 'home';

if ($pagina === 'logout') {
    session_destroy();
    header('Location: ' . BASE . 'index.php?page=login');
    exit;
}

// Se alguem tentar uma pagina que nao existe, mostra 404.
if (!array_key_exists($pagina, $rotas)) {
    $pagina = '404';
}

$titulo_pagina = $titulos[$pagina];

// Carrega o cabecalho do site.
require 'layouts/header.php';
?>

<main>
  <?php if ($pagina === '404'): ?>
    <div class="container py-5 text-center">
      <h2>Pagina nao encontrada</h2>
      <p class="text-secondary">A pagina que voce tentou acessar nao existe.</p>
      <a href="<?= BASE ?>index.php" class="btn btn-primary">Voltar para o inicio</a>
    </div>
  <?php else: ?>
    <?php require $rotas[$pagina]; ?>
  <?php endif; ?>
</main>

<?php require 'layouts/footer.php'; ?>
