<?php

declare(strict_types=1);

namespace App\Core;

abstract class Controller
{
    /** @param array<string, mixed> $data */
    protected function view(string $view, array $data = [], string $layout = 'layouts/app'): void
    {
        View::render($view, $data, $layout);
    }

    protected function redirect(string $path): never
    {
        redirect($path);
    }

    protected function requireAuth(): void
    {
        if (!Session::isAuthenticated()) {
            Session::flash('error', 'Entre para continuar.');
            $this->redirect('/login');
        }
    }

    protected function requireRole(string $role): void
    {
        $this->requireAuth();

        if (Session::role() !== $role) {
            Session::flash('error', 'Seu perfil nao tem acesso a esta area.');
            $this->redirect('/');
        }
    }

    /** @return array<string, mixed> */
    protected function input(): array
    {
        $input = $this->trimInput($_POST);

        unset($input['_token']);
        return $input;
    }

    /** @param array<string, mixed> $input @return array<string, mixed> */
    private function trimInput(array $input): array
    {
        return array_map(
            fn (mixed $value): mixed => is_array($value) ? $this->trimInput($value) : (is_string($value) ? trim($value) : $value),
            $input
        );
    }
}
