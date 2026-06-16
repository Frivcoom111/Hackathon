package com.portal.util;

import javax.swing.*; // Componentes visuais do Swing (JButton, etc.).
import java.awt.*;    // Classes gráficas básicas (Color, Font, Cursor).

/**
 * ButtonFactory: "fábrica" de botões padronizados da interface.
 *
 * PADRÃO FACTORY METHOD: em vez de configurar cor, fonte e cursor de cada botão
 * espalhado pelo código (o que geraria muita repetição e inconsistência visual),
 * centralizamos a criação aqui. Assim, todos os botões do sistema têm a mesma
 * aparência e, se quisermos mudar o estilo, mexemos em um único lugar.
 */
public class ButtonFactory {

    // Construtor privado: esta classe só oferece métodos estáticos, não deve ser instanciada.
    private ButtonFactory() {}

    /** Cria um botão PRINCIPAL — fundo azul, texto branco, em negrito.
     *  Usado para a ação mais importante da tela (ex.: "Salvar", "Confirmar"). */
    public static JButton primary(String label) {
        return build(label, new Color(0x1565C0), Color.WHITE, Font.BOLD);
    }

    /** Cria um botão de PERIGO — fundo vermelho, texto branco, em negrito.
     *  Usado para ações destrutivas/irreversíveis (ex.: "Excluir", "Bloquear"). */
    public static JButton danger(String label) {
        return build(label, new Color(0xC62828), Color.WHITE, Font.BOLD);
    }

    /** Cria um botão SECUNDÁRIO/neutro — fundo cinza claro, texto escuro, normal.
     *  Usado para ações menos importantes (ex.: "Cancelar", "Voltar"). */
    public static JButton secondary(String label) {
        return build(label, new Color(0xEEEEEE), new Color(0x333333), Font.PLAIN);
    }

    /**
     * Método interno (private) que monta o botão aplicando todas as configurações visuais.
     * Os três métodos públicos acima apenas chamam este, mudando as cores e o estilo da fonte.
     *
     * @param label     o texto que aparece no botão.
     * @param bg        a cor de fundo.
     * @param fg        a cor do texto.
     * @param fontStyle o estilo da fonte (Font.BOLD para negrito, Font.PLAIN para normal).
     */
    private static JButton build(String label, Color bg, Color fg, int fontStyle) {
        JButton btn = new JButton(label);
        btn.setBackground(bg);                 // Define a cor de fundo.
        btn.setForeground(fg);                 // Define a cor do texto.
        btn.setFont(new Font("Segoe UI", fontStyle, 13)); // Fonte "Segoe UI", tamanho 13.
        btn.setFocusPainted(false);            // Remove a borda pontilhada de foco (visual mais limpo).
        btn.setBorderPainted(false);           // Remove a borda padrão.
        btn.setOpaque(true);                   // Garante que a cor de fundo seja efetivamente pintada.
        // Faz o cursor virar uma "mãozinha" ao passar sobre o botão (indica que é clicável).
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
