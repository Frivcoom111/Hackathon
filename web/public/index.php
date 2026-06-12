<?php

declare(strict_types=1);

use App\Controllers\AuthController;
use App\Controllers\CompanyDashboardController;
use App\Controllers\HomeController;
use App\Controllers\JobController;
use App\Controllers\StudentDashboardController;
use App\Core\Env;
use App\Core\Router;
use App\Core\Session;

define('BASE_PATH', dirname(__DIR__));

$autoload = BASE_PATH . '/vendor/autoload.php';

if (file_exists($autoload)) {
    require $autoload;
} else {
    spl_autoload_register(static function (string $class): void {
        $prefix = 'App\\';

        if (!str_starts_with($class, $prefix)) {
            return;
        }

        $relativeClass = substr($class, strlen($prefix));
        $file = BASE_PATH . '/app/' . str_replace('\\', '/', $relativeClass) . '.php';

        if (file_exists($file)) {
            require $file;
        }
    });

    require BASE_PATH . '/app/Support/helpers.php';
}

Env::load(BASE_PATH . '/.env');
$GLOBALS['config'] = require BASE_PATH . '/config/app.php';
Session::start();

$router = new Router();

$router->get('/', [HomeController::class, 'index']);
$router->get('/empresas', [HomeController::class, 'companies']);
$router->get('/conteudos', [HomeController::class, 'contents']);
$router->get('/sobre', [HomeController::class, 'about']);
$router->get('/faq', [HomeController::class, 'faq']);

$router->get('/login', [AuthController::class, 'login']);
$router->post('/login', [AuthController::class, 'authenticate']);
$router->get('/logout', [AuthController::class, 'logout']);
$router->get('/cadastro/aluno', [AuthController::class, 'registerStudent']);
$router->post('/cadastro/aluno', [AuthController::class, 'storeStudent']);
$router->get('/cadastro/empresa', [AuthController::class, 'registerCompany']);
$router->post('/cadastro/empresa', [AuthController::class, 'storeCompany']);

$router->get('/vagas', [JobController::class, 'index']);
$router->get('/vagas/{id}', [JobController::class, 'show']);
$router->post('/vagas/{id}/candidatar', [JobController::class, 'apply']);

$router->get('/aluno', [StudentDashboardController::class, 'redirectToDashboard']);
$router->get('/aluno/dashboard', [StudentDashboardController::class, 'index']);
$router->get('/aluno/candidaturas', [StudentDashboardController::class, 'applications']);
$router->post('/aluno/candidaturas/{id}/cancelar', [StudentDashboardController::class, 'cancel']);
$router->get('/aluno/notificacoes', [StudentDashboardController::class, 'notifications']);
$router->post('/aluno/notificacoes/{id}/ler', [StudentDashboardController::class, 'markNotificationAsRead']);

$router->get('/empresa', [CompanyDashboardController::class, 'redirectToDashboard']);
$router->get('/empresa/dashboard', [CompanyDashboardController::class, 'index']);
$router->get('/empresa/vagas', [CompanyDashboardController::class, 'jobs']);
$router->get('/empresa/vagas/nova', [CompanyDashboardController::class, 'createJob']);
$router->post('/empresa/vagas', [CompanyDashboardController::class, 'storeJob']);
$router->get('/empresa/vagas/{id}/editar', [CompanyDashboardController::class, 'editJob']);
$router->post('/empresa/vagas/{id}', [CompanyDashboardController::class, 'updateJob']);
$router->post('/empresa/vagas/{id}/excluir', [CompanyDashboardController::class, 'deleteJob']);
$router->get('/empresa/vagas/{id}/candidatos', [CompanyDashboardController::class, 'candidates']);
$router->post('/empresa/candidaturas/{id}/status', [CompanyDashboardController::class, 'updateApplicationStatus']);

$router->dispatch($_SERVER['REQUEST_METHOD'], $_SERVER['REQUEST_URI']);
