<?php

class Empresa {
    // Status possíveis (espelhando o ENUM do banco)
    const STATUS_PENDENTE   = 'PENDING';
    const STATUS_ANALISANDO = 'ANALYSING';
    const STATUS_APROVADA   = 'APPROVED';
    const STATUS_BLOQUEADA  = 'BLOCKED';

    private string $id;
    private ?string $addressId;
    private string $nome;
    private string $cnpj;
    private ?string $descricao;
    private ?string $telefone;
    private string $status;
    private string $criadoEm;
    private string $atualizadoEm;

    public function __construct(array $dados) {
        $this->id           = $dados['id']          ?? '';
        $this->addressId    = $dados['addressId']   ?? null;
        $this->nome         = $dados['name']         ?? '';
        $this->cnpj         = $dados['cnpj']         ?? '';
        $this->descricao    = $dados['description']  ?? null;
        $this->telefone     = $dados['phone']        ?? null;
        $this->status       = $dados['status']       ?? self::STATUS_PENDENTE;
        $this->criadoEm     = $dados['createdAt']    ?? '';
        $this->atualizadoEm = $dados['updatedAt']    ?? '';
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public function getId(): string         { return $this->id; }
    public function getAddressId(): ?string { return $this->addressId; }
    public function getNome(): string       { return $this->nome; }
    public function getCnpj(): string       { return $this->cnpj; }
    public function getDescricao(): ?string { return $this->descricao; }
    public function getTelefone(): ?string  { return $this->telefone; }
    public function getStatus(): string     { return $this->status; }
    public function getCriadoEm(): string   { return $this->criadoEm; }
    public function getAtualizadoEm(): string { return $this->atualizadoEm; }

    // ── Setters ────────────────────────────────────────────────────────────────

    public function setNome(string $nome): void          { $this->nome = $nome; }
    public function setDescricao(?string $desc): void    { $this->descricao = $desc; }
    public function setTelefone(?string $tel): void      { $this->telefone = $tel; }
    public function setStatus(string $status): void      { $this->status = $status; }

    // ── Utilitários ────────────────────────────────────────────────────────────

    public function isAprovada(): bool {
        return $this->status === self::STATUS_APROVADA;
    }

    public function isBloqueada(): bool {
        return $this->status === self::STATUS_BLOQUEADA;
    }

    /**
     * CNPJ formatado: 00.000.000/0000-00
     */
    public function getCnpjFormatado(): string {
        $cnpj = preg_replace('/\D/', '', $this->cnpj);
        return preg_replace('/(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})/', '$1.$2.$3/$4-$5', $cnpj);
    }

    /**
     * Retorna os dados da empresa como array (para enviar à API)
     */
    public function toArray(): array {
        return [
            'id'          => $this->id,
            'addressId'   => $this->addressId,
            'name'        => $this->nome,
            'cnpj'        => $this->cnpj,
            'description' => $this->descricao,
            'phone'       => $this->telefone,
            'status'      => $this->status,
        ];
    }
}