<?php

declare(strict_types=1);

namespace App\Controllers;

use App\Core\Controller;
use App\Core\Session;
use App\Http\ApiException;
use App\Services\AuthService;

final class AuthController extends Controller
{
    private AuthService $auth;

    public function __construct()
    {
        $this->auth = new AuthService();
    }

    public function login(): void
    {
        $this->view('auth/login', [
            'title' => 'Login | UniALFA',
            'error' => Session::pullFlash('error'),
            'success' => Session::pullFlash('success'),
            'bodyClass' => 'auth-login-body',
            'hideChrome' => true,
        ]);
    }

    public function authenticate(): void
    {
        $input = $this->input();

        try {
            $session = $this->auth->login(
                (string) ($input['email'] ?? ''),
                (string) ($input['password'] ?? '')
            );

            Session::login($session['token'], $session['user'], $session['role']);
            $this->redirect($session['role'] === 'COMPANY' ? '/empresa/dashboard' : '/aluno/dashboard');
        } catch (ApiException $exception) {
            Session::flash('error', $exception->getMessage());
            $this->redirect('/login');
        }
    }

    public function logout(): never
    {
        Session::logout();
        Session::flash('success', 'Sessao encerrada.');
        $this->redirect('/login');
    }

    public function registerStudent(): void
    {
        $this->view('auth/register-student', [
            'title' => 'Cadastro de aluno | UniALFA',
            'error' => Session::pullFlash('error'),
        ]);
    }

    public function storeStudent(): void
    {
        try {
            $this->auth->registerStudent($this->input());
            Session::flash('success', 'Cadastro enviado. Entre para acessar o portal.');
            $this->redirect('/login');
        } catch (ApiException $exception) {
            Session::flash('error', $exception->getMessage());
            $this->redirect('/cadastro/aluno');
        }
    }

    public function registerCompany(): void
    {
        $this->view('auth/register-company', [
            'title' => 'Cadastro de empresa | UniALFA',
            'error' => Session::pullFlash('error'),
        ]);
    }

    public function storeCompany(): void
    {
        try {
            $this->auth->registerCompany($this->input());
            Session::flash('success', 'Empresa enviada para analise institucional.');
            $this->redirect('/login');
        } catch (ApiException $exception) {
            Session::flash('error', $exception->getMessage());
            $this->redirect('/cadastro/empresa');
        }
    }
}

