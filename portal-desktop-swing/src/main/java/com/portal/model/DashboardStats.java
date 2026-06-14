package com.portal.model;

public class DashboardStats {
    private final int empresasPendentes;
    private final int vagasAtivas;
    private final int candidaturasAbertas;
    private final int alunosAptos;

    public DashboardStats(int empresasPendentes, int vagasAtivas,
                          int candidaturasAbertas, int alunosAptos) {
        this.empresasPendentes   = empresasPendentes;
        this.vagasAtivas         = vagasAtivas;
        this.candidaturasAbertas = candidaturasAbertas;
        this.alunosAptos         = alunosAptos;
    }

    public int getEmpresasPendentes()   { return empresasPendentes; }
    public int getVagasAtivas()         { return vagasAtivas; }
    public int getCandidaturasAbertas() { return candidaturasAbertas; }
    public int getAlunosAptos()         { return alunosAptos; }
}
