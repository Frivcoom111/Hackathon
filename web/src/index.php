<?php
require_once __DIR__ . '/../bootstrap.php';

// Mapa de rotas: cada chave é o valor do ?page= na URL
// e o valor é o arquivo PHP que será carregado dentro do <main>
$rotas = [
    'home'              => 'pages/publico/home.php',
    'vagas'             => 'pages/publico/vagas.php',
    'login'             => 'pages/auth/login.php',
    'cadastro'          => 'pages/auth/cadastro.php',
    'perfil'            => 'pages/aluno/perfil.php',
    'notificacoes'      => 'pages/notificacoes.php',
    'empresa-dashboard'  => 'pages/empresa/dashboard.php',
    'empresa-vaga-form'  => 'pages/empresa/vaga-form.php',
    'empresa-candidatos' => 'pages/empresa/candidatos.php',
    'empresa-membros'    => 'pages/empresa/membros.php',
];

// Título que aparece na aba do navegador para cada página
$titulos = [
    'home'              => 'Início',
    'vagas'             => 'Vagas',
    'login'             => 'Login',
    'cadastro'          => 'Cadastro',
    'perfil'            => 'Meu Perfil',
    'notificacoes'      => 'Notificações',
    'empresa-dashboard'  => 'Painel da Empresa',
    'empresa-vaga-form'  => 'Vaga',
    'empresa-candidatos' => 'Candidatos',
    'empresa-membros'    => 'Membros da Empresa',
    '404'               => 'Página não encontrada',
];

// Páginas acessíveis sem login. Todo o resto exige token (a própria API
// exige token até para listar /jobs e /courses).
$rotasPublicas = ['login', 'cadastro'];

// Pega o parâmetro ?page= da URL; se não vier nada, abre a home
$pagina = $_GET['page'] ?? 'home';

if ($pagina === 'logout') {
    session_destroy();
    header('Location: ' . BASE . 'index.php?page=login');
    exit;
}

// Downloads protegidos (currículo): proxies que buscam o binário na API com o
// header Authorization e o repassam. Tratados ANTES do layout (saída binária).
if ($pagina === 'curriculo') {
    require 'pages/download/curriculo-aluno.php';
    exit;
}
if ($pagina === 'curriculo-candidato') {
    require 'pages/download/curriculo-candidato.php';
    exit;
}

// Segurança: se o usuário digitar uma rota que não existe, vai para 404
// Isso evita que alguém tente carregar arquivos arbitrários do servidor
if (!array_key_exists($pagina, $rotas)) {
    $pagina = '404';
}

// Login obrigatório: rotas que não estão na lista pública exigem usuário
// totalmente autenticado (empresa precisa ter concluído o MFA).
// A guarda fina por papel é feita dentro de cada página via App\Auth\Guard.
if ($pagina !== '404' && !in_array($pagina, $rotasPublicas, true) && !$api->jwt()->isAuthenticated()) {
    header('Location: ' . BASE . 'index.php?page=login');
    exit;
}

// Quem já está autenticado não precisa ver login/cadastro: manda para a área
// certa. (Durante o fluxo TOTP o usuário NÃO está autenticado ainda, então
// permanece na tela de login para digitar o código.)
if (in_array($pagina, $rotasPublicas, true) && $api->jwt()->isAuthenticated()) {
    header('Location: ' . BASE . 'index.php?page=' . ($api->jwt()->isCompany() ? 'empresa-dashboard' : 'home'));
    exit;
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
