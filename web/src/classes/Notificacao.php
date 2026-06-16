<?php

class Notificacao {
    // Tipos espelhando a API (notification.types.ts)
    const TIPO_NOVA_CANDIDATURA      = 'NEW_APPLICATION';
    const TIPO_STATUS_CANDIDATURA    = 'APPLICATION_STATUS';
    const TIPO_CANDIDATURA_CANCELADA = 'APPLICATION_CANCELLED';

    private string $id;
    private string $titulo;
    private string $mensagem;
    private string $tipo;
    private bool   $lida;
    private string $criadoEm;

    public function __construct(array $dados) {
        $this->id       = $dados['id']        ?? '';
        $this->titulo   = $dados['title']     ?? '';
        $this->mensagem = $dados['message']   ?? '';
        $this->tipo     = $dados['type']      ?? '';
        $this->lida     = (bool)($dados['isRead'] ?? false);
        $this->criadoEm = $dados['createdAt'] ?? '';
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public function getId(): string       { return $this->id; }
    public function getTitulo(): string   { return $this->titulo; }
    public function getMensagem(): string { return $this->mensagem; }
    public function getTipo(): string     { return $this->tipo; }
    public function isLida(): bool        { return $this->lida; }
    public function getCriadoEm(): string { return $this->criadoEm; }

    // ── Utilitários ──────────────────────────────────────────────────────────

    /** Ícone (bootstrap-icons) conforme o tipo da notificação. */
    public function getIconeClasse(): string {
        return match($this->tipo) {
            self::TIPO_NOVA_CANDIDATURA      => 'bi-person-plus',
            self::TIPO_STATUS_CANDIDATURA    => 'bi-arrow-repeat',
            self::TIPO_CANDIDATURA_CANCELADA => 'bi-x-circle',
            default                          => 'bi-bell',
        };
    }

    /** Tempo relativo simples, ex.: "agora", "há 5 min", "há 2 h", "há 3 d". */
    public function tempoRelativo(): string {
        if (!$this->criadoEm) {
            return '';
        }
        $ts = strtotime($this->criadoEm);
        if ($ts === false) {
            return '';
        }
        $diff = time() - $ts;
        if ($diff < 60)     return 'agora';
        if ($diff < 3600)   return 'há ' . intdiv($diff, 60) . ' min';
        if ($diff < 86400)  return 'há ' . intdiv($diff, 3600) . ' h';
        if ($diff < 604800) return 'há ' . intdiv($diff, 86400) . ' d';
        return date('d/m/Y', $ts);
    }
}
