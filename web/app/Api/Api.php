<?php

namespace App\Api;

use App\Auth\JwtManager;
use App\Services\AuthService;
use App\Services\CandidaturaService;
use App\Services\CompanhiaService;
use App\Services\CursoService;
use App\Services\EstudanteService;
use App\Services\NotificacaoService;
use App\Services\VagaService;

class Api
{
    private ApiClient $client;
    private JwtManager $jwtManager;

    private ?AuthService $authService = null;
    private ?CursoService $cursoService = null;
    private ?VagaService $vagaService = null;
    private ?EstudanteService $estudanteService = null;
    private ?CompanhiaService $companhiaService = null;
    private ?CandidaturaService $candidaturaService = null;
    private ?NotificacaoService $notificacaoService = null;

    public function __construct(ApiClient $client, JwtManager $jwtManager)
    {
        $this->client     = $client;
        $this->jwtManager = $jwtManager;
    }

    public function jwt(): JwtManager
    {
        return $this->jwtManager;
    }

    public function auth(): AuthService
    {
        return $this->authService ??= new AuthService($this->client, $this->jwtManager);
    }

    public function cursos(): CursoService
    {
        return $this->cursoService ??= new CursoService($this->client, $this->jwtManager);
    }

    public function vagas(): VagaService
    {
        return $this->vagaService ??= new VagaService($this->client, $this->jwtManager);
    }

    public function estudante(): EstudanteService
    {
        return $this->estudanteService ??= new EstudanteService($this->client, $this->jwtManager);
    }

    public function companhia(): CompanhiaService
    {
        return $this->companhiaService ??= new CompanhiaService($this->client, $this->jwtManager);
    }

    public function candidaturas(): CandidaturaService
    {
        return $this->candidaturaService ??= new CandidaturaService($this->client, $this->jwtManager);
    }

    public function notificacoes(): NotificacaoService
    {
        return $this->notificacaoService ??= new NotificacaoService($this->client, $this->jwtManager);
    }
}
