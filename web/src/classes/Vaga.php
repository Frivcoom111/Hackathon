<?php

class Vaga {
    // Modalidades (espelhando o ENUM do banco)
    const MODALIDADE_PRESENCIAL = 'PRESENCIAL';
    const MODALIDADE_REMOTO     = 'REMOTE';
    const MODALIDADE_HIBRIDO    = 'HYBRID';

    // Status possíveis
    const STATUS_ATIVA   = 'ACTIVE';
    const STATUS_PAUSADA = 'PAUSED';
    const STATUS_FECHADA = 'CLOSED';

    private string $id;
    private string $empresaId;
    private ?string $cursoId;
    private string $titulo;
    private string $descricao;
    private string $area;
    private ?string $requisitos;
    private ?float $salario;
    private string $localizacao;
    private string $modalidade;
    private string $status;
    private ?string $deletadoEm;
    private string $criadoEm;
    private string $atualizadoEm;

    public function __construct(array $dados) {
        $this->id           = $dados['id']           ?? '';
        $this->empresaId    = $dados['companyId']     ?? '';
        $this->cursoId      = $dados['courseId']      ?? null;
        $this->titulo       = $dados['title']         ?? '';
        $this->descricao    = $dados['description']   ?? '';
        $this->area         = $dados['area']          ?? '';
        $this->requisitos   = $dados['requirements']  ?? null;
        $this->salario      = isset($dados['salary']) ? (float)$dados['salary'] : null;
        $this->localizacao  = $dados['location']      ?? '';
        $this->modalidade   = $dados['modality']      ?? self::MODALIDADE_PRESENCIAL;
        $this->status       = $dados['status']        ?? self::STATUS_ATIVA;
        $this->deletadoEm   = $dados['deletedAt']     ?? null;
        $this->criadoEm     = $dados['createdAt']     ?? '';
        $this->atualizadoEm = $dados['updatedAt']     ?? '';
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public function getId(): string          { return $this->id; }
    public function getEmpresaId(): string   { return $this->empresaId; }
    public function getCursoId(): ?string    { return $this->cursoId; }
    public function getTitulo(): string      { return $this->titulo; }
    public function getDescricao(): string   { return $this->descricao; }
    public function getArea(): string        { return $this->area; }
    public function getRequisitos(): ?string { return $this->requisitos; }
    public function getSalario(): ?float     { return $this->salario; }
    public function getLocalizacao(): string { return $this->localizacao; }
    public function getModalidade(): string  { return $this->modalidade; }
    public function getStatus(): string      { return $this->status; }
    public function getDeletadoEm(): ?string { return $this->deletadoEm; }
    public function getCriadoEm(): string    { return $this->criadoEm; }
    public function getAtualizadoEm(): string { return $this->atualizadoEm; }

    // ── Setters ────────────────────────────────────────────────────────────────

    public function setTitulo(string $titulo): void        { $this->titulo = $titulo; }
    public function setDescricao(string $desc): void       { $this->descricao = $desc; }
    public function setArea(string $area): void            { $this->area = $area; }
    public function setRequisitos(?string $req): void      { $this->requisitos = $req; }
    public function setSalario(?float $salario): void      { $this->salario = $salario; }
    public function setLocalizacao(string $loc): void      { $this->localizacao = $loc; }
    public function setModalidade(string $mod): void       { $this->modalidade = $mod; }
    public function setStatus(string $status): void        { $this->status = $status; }

    // ── Utilitários ────────────────────────────────────────────────────────────

    public function isAtiva(): bool {
        return $this->status === self::STATUS_ATIVA;
    }

    public function isFechada(): bool {
        return $this->status === self::STATUS_FECHADA;
    }

    /**
     * Salário formatado em BRL: R$ 1.200,00
     */
    public function getSalarioFormatado(): string {
        if ($this->salario === null) return 'A combinar';
        return 'R$ ' . number_format($this->salario, 2, ',', '.');
    }

    /**
     * Modalidade em português
     */
    public function getModalidadeLabel(): string {
        return match($this->modalidade) {
            self::MODALIDADE_REMOTO  => 'Remoto',
            self::MODALIDADE_HIBRIDO => 'Híbrido',
            default                  => 'Presencial',
        };
    }

    /**
     * Retorna os dados da vaga como array (para enviar à API)
     */
    public function toArray(): array {
        return [
            'id'           => $this->id,
            'companyId'    => $this->empresaId,
            'courseId'     => $this->cursoId,
            'title'        => $this->titulo,
            'description'  => $this->descricao,
            'area'         => $this->area,
            'requirements' => $this->requisitos,
            'salary'       => $this->salario,
            'location'     => $this->localizacao,
            'modality'     => $this->modalidade,
            'status'       => $this->status,
        ];
    }
}