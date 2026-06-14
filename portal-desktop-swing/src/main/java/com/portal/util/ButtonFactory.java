package com.portal.util;

import javax.swing.*;
import java.awt.*;

/**
 * Factory Method Pattern — centraliza a criação de botões padronizados da UI.
 * Garante consistência visual sem duplicar configurações de fonte, cor e cursor.
 */
public class ButtonFactory {

    private ButtonFactory() {}

    /** Botão principal — fundo azul, texto branco, negrito. */
    public static JButton primary(String label) {
        return build(label, new Color(0x1565C0), Color.WHITE, Font.BOLD);
    }

    /** Botão de perigo — fundo vermelho, texto branco, negrito. */
    public static JButton danger(String label) {
        return build(label, new Color(0xC62828), Color.WHITE, Font.BOLD);
    }

    /** Botão neutro — fundo cinza claro, texto escuro, normal. */
    public static JButton secondary(String label) {
        return build(label, new Color(0xEEEEEE), new Color(0x333333), Font.PLAIN);
    }

    private static JButton build(String label, Color bg, Color fg, int fontStyle) {
        JButton btn = new JButton(label);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", fontStyle, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
