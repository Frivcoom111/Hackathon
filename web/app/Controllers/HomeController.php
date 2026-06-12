<?php

declare(strict_types=1);

namespace App\Controllers;

use App\Core\Controller;
use App\Services\CompanyService;
use App\Services\JobService;

final class HomeController extends Controller
{
    public function index(): void
    {
        $this->view('home/index', [
            'title' => 'Portal de Estagios UniALFA',
            'jobs' => array_slice((new JobService())->list(), 0, 3),
            'companies' => (new CompanyService())->list(),
        ]);
    }

    public function companies(): void
    {
        $this->view('home/companies', [
            'title' => 'Empresas | Portal de Estagios UniALFA',
            'companies' => (new CompanyService())->list(),
        ]);
    }

    public function contents(): void
    {
        $this->view('home/contents', [
            'title' => 'Conteudos | Portal de Estagios UniALFA',
        ]);
    }

    public function about(): void
    {
        $this->view('home/about', [
            'title' => 'Sobre | Portal de Estagios UniALFA',
        ]);
    }

    public function faq(): void
    {
        $this->view('home/faq', [
            'title' => 'FAQ | Portal de Estagios UniALFA',
        ]);
    }
}
