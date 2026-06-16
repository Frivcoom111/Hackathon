package com.portal.util;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.util.Map;

/**
 * Renderer padrão das colunas de status/situação das tabelas.
 *
 * UI limpa e padronizada: o fundo é sempre branco e quem indica o estado é
 * apenas a COR DO TEXTO. A fonte acompanha a da tabela (sem negrito), então
 * o status fica no mesmo "peso" visual das demais colunas.
 *
 * Cobre os status de Empresa, Vaga, Candidatura e Curso, além dos perfis de
 * usuário — assim todas as telas usam o mesmo renderer, sem código duplicado.
 */
public class StatusCellRenderer extends DefaultTableCellRenderer {

    private static final Color VERDE    = new Color(0x2E7D32); // positivo:    APPROVED, ACTIVE, Ativo
    private static final Color AZUL     = new Color(0x1565C0); // em andamento: ANALYSING
    private static final Color AMBAR    = new Color(0xF57F17); // aguardando:   PENDING, PAUSED
    private static final Color VERMELHO = new Color(0xC62828); // negativo:     REJECTED, BLOCKED, CLOSED, Inativo
    private static final Color CINZA    = new Color(0x757575); // neutro:       CANCELLED
    private static final Color PADRAO   = new Color(0x333333);

    /** Texto exibido na célula -> cor do texto. */
    private static final Map<String, Color> CORES = Map.ofEntries(
        Map.entry("APPROVED",  VERDE),
        Map.entry("ACTIVE",    VERDE),
        Map.entry("Ativo",     VERDE),
        Map.entry("Apto",      VERDE),
        Map.entry("Inapto",    VERMELHO),
        Map.entry("ANALYSING", AZUL),
        Map.entry("PENDING",   AMBAR),
        Map.entry("PAUSED",    AMBAR),
        Map.entry("REJECTED",  VERMELHO),
        Map.entry("BLOCKED",   VERMELHO),
        Map.entry("CLOSED",    VERMELHO),
        Map.entry("Inativo",   VERMELHO),
        Map.entry("CANCELLED", CINZA),
        // perfis de usuário
        Map.entry("Administrador", AZUL),
        Map.entry("Aluno",         VERDE),
        Map.entry("Recrutador",    AMBAR)
    );

    public StatusCellRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setFont(table.getFont()); // mesma fonte da tabela — sem negrito
        if (!isSelected) {
            setBackground(Color.WHITE);
            String texto = value != null ? value.toString() : "";
            setForeground(CORES.getOrDefault(texto, PADRAO));
        }
        return this;
    }
}
