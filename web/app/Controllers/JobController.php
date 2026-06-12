<?php

declare(strict_types=1);

namespace App\Controllers;

use App\Core\Controller;
use App\Core\Session;
use App\Services\ApplicationService;
use App\Services\JobService;

final class JobController extends Controller
{
    private JobService $jobs;
    private ApplicationService $applications;

    public function __construct()
    {
        $this->jobs = new JobService();
        $this->applications = new ApplicationService();
    }

    public function index(): void
    {
        $this->view('jobs/index', [
            'title' => 'Vagas | UniALFA',
            'jobs' => $this->jobs->list($_GET),
            'filters' => $_GET,
            'success' => Session::pullFlash('success'),
            'error' => Session::pullFlash('error'),
        ]);
    }

    public function show(string $id): void
    {
        $job = $this->jobs->find($id);

        if ($job === null) {
            http_response_code(404);
            $this->view('home/not-found', ['title' => 'Vaga nao encontrada']);
            return;
        }

        $this->view('jobs/show', [
            'title' => $job->title() . ' | UniALFA',
            'job' => $job,
            'related' => array_slice($this->jobs->list(['area' => $job->area()]), 0, 2),
            'error' => Session::pullFlash('error'),
        ]);
    }

    public function apply(string $id): void
    {
        if (!Session::isAuthenticated()) {
            Session::flash('error', 'Entre como aluno para se candidatar.');
            $this->redirect('/login');
        }

        if (Session::role() !== 'STUDENT') {
            Session::flash('error', 'Somente alunos podem se candidatar.');
            $this->redirect('/vagas/' . $id);
        }

        $this->applications->apply($id, (string) ($_POST['coverLetter'] ?? ''));
        Session::flash('success', 'Candidatura enviada com sucesso.');
        $this->redirect('/aluno/candidaturas');
    }
}
