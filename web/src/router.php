<?php
$uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
$uri = trim($uri, '/');

// Remove o prefixo do projeto se necessário
$paginas = ['home', 'cadastro', 'login', 'vagas', 'empresas', 'alunos'];

if (in_array($uri, $paginas)) {
    $_GET['page'] = $uri;
    require 'index.php';
} else {
    return false; // Deixa o PHP servir normalmente
}