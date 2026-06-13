package com.portal.gui.login;

import com.portal.gui.dashboard.DashboardFrame;
import com.portal.model.User;
import com.portal.service.AuthException;
import com.portal.service.AuthService;
import com.portal.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField senhaField;
    private final AuthService authService = new AuthService();


    public LoginFrame() {
        setTitle("Portal UniALFA");
        setSize(420, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // --- header azul ---
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(0x1565C0));
        header.setPreferredSize(new Dimension(420, 140));

        JLabel titulo = new JLabel("Portal UniALFA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Sistema de Gestão de Estágios");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(0xBBDEFB));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(2, 0, 2, 0);
        header.add(titulo, gbc);
        gbc.gridy = 1;
        header.add(subtitulo, gbc);

        // --- formulário ---
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(32, 48, 32, 48));

        emailField = new JTextField();
        senhaField = new JPasswordField();
        JButton entrarBtn = new JButton("Entrar");

        estilizarCampo(emailField);
        estilizarCampo(senhaField);
        estilizarBotao(entrarBtn);

        form.add(label("E-mail"));
        form.add(Box.createVerticalStrut(6));
        form.add(emailField);
        form.add(Box.createVerticalStrut(18));
        form.add(label("Senha"));
        form.add(Box.createVerticalStrut(6));
        form.add(senhaField);
        form.add(Box.createVerticalStrut(28));
        form.add(entrarBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        add(root);

        entrarBtn.addActionListener(e -> realizarLogin());
        getRootPane().setDefaultButton(entrarBtn);
    }

    private void realizarLogin() {
        String email = emailField.getText().trim();
        String senha = new String(senhaField.getPassword());
        try {
            User user = authService.login(email, senha);
            Session.setCurrentUser(user);
            dispose();
            new DashboardFrame().setVisible(true);
        } catch (AuthException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(new Color(0x616161));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xBDBDBD)),
            new EmptyBorder(4, 10, 4, 10)
        ));
    }

    private void estilizarBotao(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(0x1565C0));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    }
}
