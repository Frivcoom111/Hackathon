package com.portal.gui.applications;

import com.portal.model.Application;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ApplicationDetailDialog extends JDialog {

    public ApplicationDetailDialog(Frame parent, Application candidatura) {
        super(parent, "Detalhes da Candidatura", true);
        setSize(440, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
        build(candidatura);
    }

    private void build(Application candidatura) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Cabeçalho ─────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0xE65100));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitulo = new JLabel("Candidatura");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblStatus = buildStatusBadge(candidatura.getStatus().name());

        header.add(lblTitulo, BorderLayout.WEST);
        header.add(lblStatus, BorderLayout.EAST);

        // ── Campos ────────────────────────────────────────────────────────────
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(Color.WHITE);
        campos.setBorder(new EmptyBorder(20, 24, 10, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 0, 8, 16);
        gc.anchor = GridBagConstraints.WEST;

        String aluno = candidatura.getStudentName() != null
            ? candidatura.getStudentName() : candidatura.getStudentId();
        String vaga  = candidatura.getJobTitle() != null
            ? candidatura.getJobTitle() : candidatura.getJobId();

        String[][] dados = {
            {"Aluno:",  aluno},
            {"Vaga:",   vaga},
            {"Status:", candidatura.getStatus().name()},
        };

        for (int i = 0; i < dados.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            JLabel lbl = new JLabel(dados[i][0]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setForeground(new Color(0x555555));
            campos.add(lbl, gc);

            gc.gridx = 1; gc.weightx = 1;
            JLabel val = new JLabel(dados[i][1] != null ? dados[i][1] : "—");
            val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            campos.add(val, gc);
        }

        // ── Rodapé ────────────────────────────────────────────────────────────
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        rodape.setBackground(new Color(0xF5F5F5));
        rodape.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xE0E0E0)));

        JButton fecharBtn = new JButton("Fechar");
        fecharBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fecharBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fecharBtn.addActionListener(e -> dispose());

        rodape.add(fecharBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(campos, BorderLayout.CENTER);
        root.add(rodape, BorderLayout.SOUTH);
        add(root);
    }

    private JLabel buildStatusBadge(String status) {
        JLabel lbl = new JLabel(status);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setOpaque(true);
        lbl.setBorder(new EmptyBorder(3, 10, 3, 10));
        switch (status) {
            case "PENDING"   -> { lbl.setBackground(new Color(0xFFF9C4)); lbl.setForeground(new Color(0xF57F17)); }
            case "ANALYSING" -> { lbl.setBackground(new Color(0xE3F2FD)); lbl.setForeground(new Color(0x1565C0)); }
            case "APPROVED"  -> { lbl.setBackground(new Color(0xE8F5E9)); lbl.setForeground(new Color(0x2E7D32)); }
            case "REJECTED"  -> { lbl.setBackground(new Color(0xFFEBEE)); lbl.setForeground(new Color(0xC62828)); }
            case "CANCELLED" -> { lbl.setBackground(new Color(0xF5F5F5)); lbl.setForeground(new Color(0x757575)); }
            default          -> { lbl.setBackground(Color.WHITE);         lbl.setForeground(Color.BLACK); }
        }
        return lbl;
    }
}
