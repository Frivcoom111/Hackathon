<?php

declare(strict_types=1);

namespace App\Controllers;

use App\Core\Controller;
use App\Core\Session;
use App\Services\ApplicationService;
use App\Services\JobService;
use App\Services\NotificationService;

final class StudentDashboardController extends Controller
{
    private ApplicationService $applications;
    private NotificationService $notifications;

    public function __construct()
    {
        $this->applications = new ApplicationService();
        $this->notifications = new NotificationService();
    }

    public function redirectToDashboard(): never
    {
        $this->redirect('/aluno/dashboard');
    }

    public function index(): void
    {
        $this->requireRole('STUDENT');

        $applications = $this->applications->mine();
        $notifications = $this->notifications->mine();

        $this->view('student/dashboard', [
            'title' => 'Painel do aluno | UniALFA',
            'applications' => $applications,
            'notifications' => $notifications,
            'jobs' => array_slice((new JobService())->list(), 0, 3),
            'success' => Session::pullFlash('success'),
        ]);
    }

    public function applications(): void
    {
        $this->requireRole('STUDENT');

        $this->view('student/applications', [
            'title' => 'Minhas candidaturas | UniALFA',
            'applications' => $this->applications->mine(),
            'success' => Session::pullFlash('success'),
        ]);
    }

    public function cancel(string $id): void
    {
        $this->requireRole('STUDENT');
        $this->applications->cancel($id);
        Session::flash('success', 'Candidatura cancelada.');
        $this->redirect('/aluno/candidaturas');
    }

    public function notifications(): void
    {
        $this->requireRole('STUDENT');

        $this->view('student/notifications', [
            'title' => 'Notificacoes | UniALFA',
            'notifications' => $this->notifications->mine(),
            'success' => Session::pullFlash('success'),
        ]);
    }

    public function markNotificationAsRead(string $id): void
    {
        $this->requireRole('STUDENT');
        $this->notifications->markAsRead($id);
        Session::flash('success', 'Notificacao marcada como lida.');
        $this->redirect('/aluno/notificacoes');
    }
}
