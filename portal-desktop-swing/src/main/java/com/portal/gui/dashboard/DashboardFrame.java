package com.portal.gui.dashboard;

import com.portal.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        String role = Session.getCurrentUser().getRole().name();
        String email = Session.getCurrentUser().getEmail();

        setTitle("Portal UniALFA — " + role);
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // Barra superior
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(0x1565C0));
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titulo = new JLabel("Portal UniALFA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);

        JLabel userInfo = new JLabel(email + "  |  " + role);
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userInfo.setForeground(new Color(0xBBDEFB));

        JButton sairBtn = new JButton("Sair");
        sairBtn.setBackground(new Color(0x0D47A1));
        sairBtn.setForeground(Color.WHITE);
        sairBtn.setFocusPainted(false);
        sairBtn.setBorderPainted(false);
        sairBtn.setOpaque(true);
        sairBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sairBtn.addActionListener(e -> {
            Session.clear();
            dispose();
            new com.portal.gui.login.LoginFrame().setVisible(true);
        });

        topBar.add(titulo, BorderLayout.WEST);
        topBar.add(userInfo, BorderLayout.CENTER);
        topBar.add(sairBtn, BorderLayout.EAST);

        // Conteúdo central (placeholder)
        JLabel placeholder = new JLabel("Bem-vindo, " + email + "!", SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        placeholder.setForeground(new Color(0x424242));

        root.add(topBar, BorderLayout.NORTH);
        root.add(placeholder, BorderLayout.CENTER);
        add(root);
    }
}
