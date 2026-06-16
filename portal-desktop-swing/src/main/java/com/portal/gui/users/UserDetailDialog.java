package com.portal.gui.users;

import com.portal.model.Address;
import com.portal.model.User;
import com.portal.util.ButtonFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * UserDetailDialog: diálogo (pop-up) com os detalhes de um usuário.
 *
 * Novidade em relação aos outros diálogos: usa ABAS (JTabbedPane) para separar os
 * "Dados" do usuário e o "Endereço" em duas guias diferentes dentro do mesmo pop-up.
 */
public class UserDetailDialog extends JDialog {

    public UserDetailDialog(Frame parent, User user) {
        super(parent, "Detalhes do Usuário", true);
        setSize(460, 360);
        setLocationRelativeTo(parent);
        setResizable(false);
        build(user);
    }

    /** Monta o diálogo: cabeçalho, abas (Dados/Endereço) e rodapé. */
    private void build(User user) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Cabeçalho: e-mail + perfil ────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x1565C0));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblEmail = new JLabel(user.getEmail());
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblEmail.setForeground(Color.WHITE);

        JLabel lblRole = new JLabel(rotuloRole(user));
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRole.setForeground(new Color(0xBBDEFB));

        header.add(lblEmail, BorderLayout.WEST);
        header.add(lblRole,  BorderLayout.EAST);

        // ── Abas: cada aba é um painel separado ───────────────────────────────
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        abas.addTab("Dados",     buildDadosTab(user));
        abas.addTab("Endereço",  buildEnderecoTab(user.getAddress()));

        // ── Rodapé: botão "Fechar" ────────────────────────────────────────────
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        rodape.setBackground(new Color(0xF5F5F5));
        rodape.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xE0E0E0)));

        JButton fecharBtn = ButtonFactory.secondary("Fechar");
        fecharBtn.addActionListener(e -> dispose());
        rodape.add(fecharBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(abas,   BorderLayout.CENTER);
        root.add(rodape, BorderLayout.SOUTH);
        add(root);
    }

    /** Monta a aba "Dados", com e-mail, perfil e o status colorido (verde/vermelho). */
    private JPanel buildDadosTab(User user) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(7, 0, 7, 12);
        gc.anchor = GridBagConstraints.WEST;

        // Decide o texto e a cor do status conforme o usuário esteja ativo ou não.
        String statusTxt = user.isActive() ? "Ativo" : "Inativo";
        Color  statusCor = user.isActive() ? new Color(0x2E7D32) : new Color(0xC62828);

        // Pares "rótulo: valor" exibidos primeiro (e-mail e perfil).
        String[][] dados = {
            {"E-mail:", user.getEmail()},
            {"Perfil:", rotuloRole(user)},
        };

        for (int i = 0; i < dados.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            JLabel lbl = new JLabel(dados[i][0]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setForeground(new Color(0x555555));
            lbl.setPreferredSize(new Dimension(70, 24));
            panel.add(lbl, gc);

            gc.gridx = 1; gc.weightx = 1;
            JLabel val = new JLabel(dados[i][1]);
            val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(val, gc);
        }

        // Linha do "Status" tratada à parte, porque o valor é colorido.
        gc.gridx = 0; gc.gridy = dados.length; gc.weightx = 0;
        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setForeground(new Color(0x555555));
        lblStatus.setPreferredSize(new Dimension(70, 24));
        panel.add(lblStatus, gc);

        gc.gridx = 1; gc.weightx = 1;
        JLabel valStatus = new JLabel(statusTxt);
        valStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valStatus.setForeground(statusCor); // Verde se ativo, vermelho se inativo.
        panel.add(valStatus, gc);

        return panel;
    }

    /**
     * Monta a aba "Endereço". Se o usuário não tiver endereço (a == null),
     * exibe apenas uma mensagem informando isso.
     */
    private JPanel buildEnderecoTab(Address a) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Caso não haja endereço cadastrado.
        if (a == null) {
            JLabel sem = new JLabel("Sem endereço cadastrado.");
            sem.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            sem.setForeground(Color.GRAY);
            panel.add(sem);
            return panel;
        }

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 0, 6, 12);
        gc.anchor = GridBagConstraints.WEST;

        // Monta o logradouro, acrescentando o complemento somente se houver.
        String logradouro = a.getStreet() + ", " + a.getNumber()
            + (a.getComplement() != null && !a.getComplement().isBlank() ? " — " + a.getComplement() : "");

        String[][] dados = {
            {"Logradouro:", logradouro},
            {"Bairro:",     a.getDistrict()},
            {"Cidade/UF:",  a.getCity() + " / " + a.getState()},
            {"CEP:",        a.formatarCep()}, // CEP já formatado (00000-000).
        };

        for (int i = 0; i < dados.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            JLabel lbl = new JLabel(dados[i][0]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setForeground(new Color(0x555555));
            lbl.setPreferredSize(new Dimension(90, 24));
            panel.add(lbl, gc);

            gc.gridx = 1; gc.weightx = 1;
            JLabel val = new JLabel(dados[i][1] != null ? dados[i][1] : "—");
            val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(val, gc);
        }
        return panel;
    }

    /** Traduz o perfil (enum Role) para um rótulo amigável em português. */
    private String rotuloRole(User user) {
        return switch (user.getRole()) {
            case ADMIN   -> "Administrador";
            case STUDENT -> "Aluno";
            case COMPANY -> "Recrutador";
        };
    }
}
