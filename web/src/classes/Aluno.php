<?php

class Aluno {
    private string $id;
    private string $userId;
    private ?string $addressId;
    private string $nome;
    private string $ra;
    private string $cpf;
    private ?string $telefone;
    private bool $elegivel;
    private string $criadoEm;
    private string $atualizadoEm;

    public function __construct(array $dados) {
        $this->id           = $dados['id']          ?? '';
        $this->userId       = $dados['userId']       ?? '';
        $this->addressId    = $dados['addressId']    ?? null;
        $this->nome         = $dados['name']         ?? '';
        $this->ra           = $dados['ra']           ?? '';
        $this->cpf          = $dados['cpf']          ?? '';
        $this->telefone     = $dados['phone']        ?? null;
        $this->elegivel     = (bool)($dados['isEligible'] ?? true);
        $this->criadoEm     = $dados['createdAt']    ?? '';
        $this->atualizadoEm = $dados['updatedAt']    ?? '';
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public function getId(): string        { return $this->id; }
    public function getUserId(): string    { return $this->userId; }
    public function getAddressId(): ?string { return $this->addressId; }
    public function getNome(): string      { return $this->nome; }
    public function getRa(): string        { return $this->ra; }
    public function getCpf(): string       { return $this->cpf; }
    public function getTelefone(): ?string { return $this->telefone; }
    public function isElegivel(): bool     { return $this->elegivel; }
    public function getCriadoEm(): string  { return $this->criadoEm; }
    public function getAtualizadoEm(): string { return $this->atualizadoEm; }

    // ── Setters ────────────────────────────────────────────────────────────────

    public function setNome(string $nome): void        { $this->nome = $nome; }
    public function setTelefone(?string $tel): void    { $this->telefone = $tel; }
    public function setElegivel(bool $val): void       { $this->elegivel = $val; }

    // ── Utilitários ────────────────────────────────────────────────────────────

    /**
     * Retorna os dados do aluno como array (para enviar à API)
     */
    public function toArray(): array {
        return [
            'id'         => $this->id,
            'userId'     => $this->userId,
            'addressId'  => $this->addressId,
            'name'       => $this->nome,
            'ra'         => $this->ra,
            'cpf'        => $this->cpf,
            'phone'      => $this->telefone,
            'isEligible' => $this->elegivel,
        ];
    }

    /**
     * CPF formatado: 000.000.000-00
     */
    public function getCpfFormatado(): string {
        $cpf = preg_replace('/\D/', '', $this->cpf);
        return preg_replace('/(\d{3})(\d{3})(\d{3})(\d{2})/', '$1.$2.$3-$4', $cpf);
    }
}
