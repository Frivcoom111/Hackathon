<?php
define('BASE', '');

// Mapa de rotas: chave = URL, valor = arquivo
$rotas = [
    'home'      => 'pages/home.php',
    'cadastro'  => 'pages/cadastro.php',
    'login'     => 'pages/login.php',
    'vagas'     => 'pages/vagas.php',
    'empresas'  => 'pages/empresas.php',
    'alunos'    => 'pages/alunos.php',
    'perfil'    => 'pages/perfil.php',
];

// Títulos de cada página
$titulos = [
    'home'      => 'Início',
    'cadastro'  => 'Cadastro',
    'login'     => 'Login',
    'vagas'     => 'Vagas',
    'empresas'  => 'Empresas',
    'alunos'    => 'Alunos',
    'perfil'    => 'Meu Perfil',
];

// Pega a página da URL, padrão é home
$pagina = $_GET['page'] ?? 'home';

// Segurança: só permite rotas cadastradas
if (!array_key_exists($pagina, $rotas)) {
    $pagina = '404';
}

$titulo_pagina = $titulos[$pagina];
$arquivo = $rotas[$pagina];

require 'layouts/header.php';
?>

<main>
  <?php require $arquivo; ?>
</main>

<?php require 'layouts/footer.php'; ?>