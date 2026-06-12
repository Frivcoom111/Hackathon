<?php

declare(strict_types=1);

namespace App\Controllers;

use App\Core\Controller;
use App\Core\Session;
use App\Services\ApplicationService;
use App\Services\JobService;

final class CompanyDashboardController extends Controller
{
    private JobService $jobs;
    private ApplicationService $applications;

    public function __construct()
    {
        $this->jobs = new JobService();
        $this->applications = new ApplicationService();
    }

    public function redirectToDashboard(): never
    {
        $this->redirect('/empresa/dashboard');
    }

    public function index(): void
    {
        $this->requireRole('COMPANY');

        $jobs = $this->jobs->listCompany(['status' => 'ACTIVE']);
        $firstJobId = $jobs !== [] ? $jobs[0]->id() : 'job-backend-jr';

        $this->view('company/dashboard', [
            'title' => 'Painel da empresa | UniALFA',
            'jobs' => $jobs,
            'applications' => $this->applications->byJob($firstJobId),
            'success' => Session::pullFlash('success'),
        ]);
    }

    public function jobs(): void
    {
        $this->requireRole('COMPANY');

        $this->view('company/jobs', [
            'title' => 'Minhas vagas | UniALFA',
            'jobs' => $this->jobs->listCompany(),
            'success' => Session::pullFlash('success'),
        ]);
    }

    public function createJob(): void
    {
        $this->requireRole('COMPANY');

        $this->view('company/job-form', [
            'title' => 'Nova vaga | UniALFA',
            'job' => null,
            'action' => url('/empresa/vagas'),
        ]);
    }

    public function storeJob(): void
    {
        $this->requireRole('COMPANY');
        $this->jobs->create($this->input());
        Session::flash('success', 'Vaga criada com sucesso.');
        $this->redirect('/empresa/vagas');
    }

    public function editJob(string $id): void
    {
        $this->requireRole('COMPANY');

        $this->view('company/job-form', [
            'title' => 'Editar vaga | UniALFA',
            'job' => $this->jobs->findCompany($id),
            'action' => url('/empresa/vagas/' . $id),
        ]);
    }

    public function updateJob(string $id): void
    {
        $this->requireRole('COMPANY');
        $this->jobs->update($id, $this->input());
        Session::flash('success', 'Vaga atualizada com sucesso.');
        $this->redirect('/empresa/vagas');
    }

    public function deleteJob(string $id): void
    {
        $this->requireRole('COMPANY');
        $this->jobs->delete($id);
        Session::flash('success', 'Vaga excluida.');
        $this->redirect('/empresa/vagas');
    }

    public function candidates(string $id): void
    {
        $this->requireRole('COMPANY');

        $this->view('company/candidates', [
            'title' => 'Candidatos por vaga | UniALFA',
            'job' => $this->jobs->findCompany($id),
            'applications' => $this->applications->byJob($id),
            'success' => Session::pullFlash('success'),
        ]);
    }

    public function updateApplicationStatus(string $id): void
    {
        $this->requireRole('COMPANY');
        $status = (string) ($_POST['status'] ?? 'ANALYSING');
        $jobId = (string) ($_POST['jobId'] ?? '');
        $this->applications->updateStatus($id, $status, $jobId);
        Session::flash('success', 'Status da candidatura atualizado.');
        $redirectTo = (string) ($_POST['returnTo'] ?? '/empresa/dashboard');
        $this->redirect(str_starts_with($redirectTo, '/') ? $redirectTo : '/empresa/dashboard');
    }
}
