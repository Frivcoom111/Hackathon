package com.portal.util;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer; // Classe base para customizar células de tabela.
import java.awt.Color;
import java.awt.Component;
import java.util.Map;

/**
 * StatusCellRenderer: define COMO desenhar as células de "status/situação" das tabelas.
 *
 * O QUE É UM RENDERER? No Swing, um "renderer" é um objeto que decide a aparência de
 * cada célula de uma tabela (cor, alinhamento, fonte). Esta classe estende o renderer
 * padrão para colorir o TEXTO do status de acordo com o seu significado.
 *
 * DECISÃO DE DESIGN: o fundo é sempre branco; quem indica o estado é apenas a COR DO
 * TEXTO. Isso deixa a interface limpa e padronizada. Um único renderer atende todas
 * as telas (Empresa, Vaga, Candidatura, Curso e perfis de usuário), evitando código repetido.
 */
public class StatusCellRenderer extends DefaultTableCellRenderer {

    // Paleta de cores semânticas: cada cor comunica um "tipo" de situação.
    private static final Color VERDE    = new Color(0x2E7D32); // positivo:    APPROVED, ACTIVE, Ativo
    private static final Color AZUL     = new Color(0x1565C0); // em andamento: ANALYSING
    private static final Color AMBAR    = new Color(0xF57F17); // aguardando:   PENDING, PAUSED
    private static final Color VERMELHO = new Color(0xC62828); // negativo:     REJECTED, BLOCKED, CLOSED, Inativo
    private static final Color CINZA    = new Color(0x757575); // neutro:       CANCELLED
    private static final Color PADRAO   = new Color(0x333333); // cor usada quando o status não está no mapa.

    // Mapa (dicionário) que associa cada texto de status à sua cor.
    // Map.ofEntries cria um mapa imutável (fixo) de pares "texto -> cor".
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

    /** Construtor: centraliza horizontalmente o texto em todas as células de status. */
    public StatusCellRenderer() {
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Método chamado pela tabela para CADA célula que usa este renderer.
     * É aqui que decidimos a cor do texto com base no valor da célula.
     *
     * @Override indica que estamos substituindo o comportamento padrão da classe-mãe.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        // Primeiro deixa a classe-mãe fazer a configuração básica da célula.
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setFont(table.getFont()); // Usa a mesma fonte da tabela — sem negrito, visual uniforme.

        // Só personalizamos a cor quando a linha NÃO está selecionada
        // (linha selecionada já tem o destaque azul padrão do Swing).
        if (!isSelected) {
            setBackground(Color.WHITE); // Fundo sempre branco.
            String texto = value != null ? value.toString() : "";
            // Busca a cor correspondente ao texto; se não achar, usa a cor PADRÃO.
            setForeground(CORES.getOrDefault(texto, PADRAO));
        }
        return this; // Devolve o próprio componente já configurado para ser desenhado.
    }
}
