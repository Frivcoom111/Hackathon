<?php
// Inicia a sessão para guardar o token e dados do usuário logado no servidor
session_start();

// BASE é o prefixo usado nos links e assets (ex: imagens, CSS, JS)
// Deixamos vazio porque o index.php fica na raiz do projeto
define('BASE', '');

// Mapa de rotas: cada chave é o valor do ?page= na URL
// e o valor é o arquivo PHP que será carregado dentro do <main>
$rotas = [
    'home'      => 'pages/home.php',
    'cadastro'  => 'pages/cadastro.php',
    'login'     => 'pages/login.php',
    'vagas'     => 'pages/vagas.php',
    'empresas'  => 'pages/empresas.php',
    // 'alunos' removido — gerenciado pelo back office Java, não pelo portal web
    'perfil'    => 'pages/perfil.php',
];

// Título que aparece na aba do navegador para cada página
$titulos = [
    'home'      => 'Início',
    'cadastro'  => 'Cadastro',
    'login'     => 'Login',
    'vagas'     => 'Vagas',
    'empresas'  => 'Empresas',
    // 'alunos' removido — mesma razão acima
    'perfil'    => 'Meu Perfil',
    '404'       => 'Página não encontrada',
];

// Pega o parâmetro ?page= da URL; se não vier nada, abre a home
$pagina = $_GET['page'] ?? 'home';

// Segurança: se o usuário digitar uma rota que não existe, vai para 404
// Isso evita que alguém tente carregar arquivos arbitrários do servidor
if (!array_key_exists($pagina, $rotas)) {
    $pagina = '404';
}

$titulo_pagina = $titulos[$pagina];

// Carrega o cabeçalho (navbar + <head> do HTML)
require 'layouts/header.php';
?>

<main>
  <?php
  // Se for 404, mostra mensagem direto aqui sem precisar de arquivo separado
  if ($pagina === '404'):
  ?>
    <div class="container py-5 text-center">
      <h2>Página não encontrada</h2>
      <p class="text-secondary">A página que você tentou acessar não existe.</p>
      <a href="<?= BASE ?>index.php" class="btn btn-primary">Voltar para o início</a>
    </div>
  <?php
  else:
    // Carrega o arquivo da página correspondente à rota
    require $rotas[$pagina];
  endif;
  ?>
</main>

<?php require 'layouts/footer.php'; ?>