package com.portal.model;

/**
 * Classe DashboardStats (Estatísticas do Painel): agrupa os números-resumo que
 * aparecem na tela inicial (dashboard) do sistema.
 *
 * É um objeto "somente leitura": repare que todos os atributos são "final" e só há
 * getters (nenhum setter). Isso significa que, uma vez criado com os valores, ele
 * NÃO pode ser alterado. Esse padrão é chamado de objeto imutável e é seguro porque
 * os números do painel são apenas um retrato do momento em que foram calculados.
 */
public class DashboardStats {
    // ===== ATRIBUTOS (todos "final" = não mudam depois de criados) =====
    private final int empresasPendentes;    // Quantidade de empresas aguardando aprovação.
    private final int vagasAtivas;          // Quantidade de vagas atualmente ativas/abertas.
    private final int candidaturasAbertas;  // Quantidade de candidaturas em andamento.
    private final int alunosAptos;          // Quantidade de alunos aptos a se candidatar.

    /**
     * Construtor: a única forma de definir os valores (não há setters).
     * Todos os números são informados de uma vez, no momento da criação.
     */
    public DashboardStats(int empresasPendentes, int vagasAtivas,
                          int candidaturasAbertas, int alunosAptos) {
        this.empresasPendentes   = empresasPendentes;
        this.vagasAtivas         = vagasAtivas;
        this.candidaturasAbertas = candidaturasAbertas;
        this.alunosAptos         = alunosAptos;
    }

    // ===== GETTERS: única forma de LER os valores =====
    public int getEmpresasPendentes()   { return empresasPendentes; }
    public int getVagasAtivas()         { return vagasAtivas; }
    public int getCandidaturasAbertas() { return candidaturasAbertas; }
    public int getAlunosAptos()         { return alunosAptos; }
}
