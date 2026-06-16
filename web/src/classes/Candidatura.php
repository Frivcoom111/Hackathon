<?php

class Candidatura {
    // Status possíveis (espelhando o ENUM do banco)
    const STATUS_PENDENTE   = 'PENDING';
    const STATUS_ANALISANDO = 'ANALYSING';
    const STATUS_APROVADA   = 'APPROVED';
    const STATUS_REJEITADA  = 'REJECTED';
    const STATUS_CANCELADA  = 'CANCELLED';

    private string $id;
    private string $alunoId;
    private string $vagaId;
    private string $status;
    private ?string $cartaApresentacao;
    private ?string $deletadoEm;
    private string $criadoEm;
    private string $atualizadoEm;

    // Dados do candidato (vêm aninhados em student na listagem da empresa)
    private string $alunoNome;
    private string $alunoRa;
    private string $alunoEmail;
    private string $alunoTelefone;
    private string $alunoCurso;
    private bool   $temCurriculo;

    public function __construct(array $dados) {
        $this->id                 = $dados['id']          ?? '';
        $this->alunoId            = $dados['studentId']   ?? '';
        $this->vagaId             = $dados['jobId']       ?? '';
        $this->status             = $dados['status']      ?? self::STATUS_PENDENTE;
        $this->cartaApresentacao  = $dados['coverLetter'] ?? null;
        $this->deletadoEm         = $dados['deletedAt']   ?? null;
        $this->criadoEm           = $dados['createdAt']   ?? '';
        $this->atualizadoEm       = $dados['updatedAt']   ?? '';

        $student = $dados['student'] ?? [];
        $this->alunoNome     = $student['name']  ?? 'Aluno';
        $this->alunoRa       = $student['ra']    ?? '';
        $this->alunoEmail    = $student['user']['email'] ?? '';
        $this->alunoTelefone = $student['phone'] ?? '';
        $this->alunoCurso    = $student['courses'][0]['course']['name'] ?? '';
        // Currículo da candidatura, com fallback para o do perfil do aluno.
        $this->temCurriculo  = !empty($dados['resumePath']) || !empty($student['resumePath']);
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public function getId(): string                   { return $this->id; }
    public function getAlunoId(): string              { return $this->alunoId; }
    public function getVagaId(): string               { return $this->vagaId; }
    public function getStatus(): string               { return $this->status; }
    public function getCartaApresentacao(): ?string   { return $this->cartaApresentacao; }
    public function getDeletadoEm(): ?string          { return $this->deletadoEm; }
    public function getCriadoEm(): string             { return $this->criadoEm; }
    public function getAtualizadoEm(): string         { return $this->atualizadoEm; }

    public function getAlunoNome(): string            { return $this->alunoNome; }
    public function getAlunoRa(): string              { return $this->alunoRa; }
    public function getAlunoEmail(): string           { return $this->alunoEmail; }
    public function getAlunoTelefone(): string        { return $this->alunoTelefone; }
    public function getAlunoCurso(): string           { return $this->alunoCurso; }
    public function temCurriculo(): bool              { return $this->temCurriculo; }

    // ── Setters ────────────────────────────────────────────────────────────────

    public function setStatus(string $status): void              { $this->status = $status; }
    public function setCartaApresentacao(?string $carta): void   { $this->cartaApresentacao = $carta; }

    // ── Utilitários ────────────────────────────────────────────────────────────

    public function isPendente(): bool   { return $this->status === self::STATUS_PENDENTE; }
    public function isAprovada(): bool   { return $this->status === self::STATUS_APROVADA; }
    public function isRejeitada(): bool  { return $this->status === self::STATUS_REJEITADA; }
    public function isCancelada(): bool  { return $this->status === self::STATUS_CANCELADA; }

    /**
     * Status em português com badge color
     */
    public function getStatusLabel(): string {
        return match($this->status) {
            self::STATUS_ANALISANDO => 'Em análise',
            self::STATUS_APROVADA   => 'Aprovada',
            self::STATUS_REJEITADA  => 'Rejeitada',
            self::STATUS_CANCELADA  => 'Cancelada',
            default                 => 'Pendente',
        };
    }

    /**
     * Classe CSS do badge conforme status
     */
    public function getStatusBadgeClass(): string {
        return match($this->status) {
            self::STATUS_APROVADA   => 'badge-success',
            self::STATUS_REJEITADA  => 'badge-danger',
            self::STATUS_CANCELADA  => 'badge-secondary',
            self::STATUS_ANALISANDO => 'badge-warning',
            default                 => 'badge-info',
        };
    }

    /**
     * Retorna os dados da candidatura como array (para enviar à API)
     */
    public function toArray(): array {
        return [
            'id'          => $this->id,
            'studentId'   => $this->alunoId,
            'jobId'       => $this->vagaId,
            'status'      => $this->status,
            'coverLetter' => $this->cartaApresentacao,
        ];
    }
}
