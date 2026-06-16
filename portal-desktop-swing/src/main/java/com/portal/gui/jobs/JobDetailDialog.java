package com.portal.gui.jobs;

import com.portal.model.Job;
import com.portal.util.ButtonFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class JobDetailDialog extends JDialog {

    public JobDetailDialog(Frame parent, Job vaga) {
        super(parent, "Detalhes da Vaga", true);
        setSize(460, 380);
        setLocationRelativeTo(parent);
        setResizable(false);
        build(vaga);
    }

    private void build(Job vaga) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Cabeçalho ─────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x1565C0));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitulo = new JLabel(vaga.getTitle());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblStatus = new JLabel(vaga.getStatus().name());
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setForeground(new Color(0xBBDEFB));

        header.add(lblTitulo, BorderLayout.WEST);
        header.add(lblStatus, BorderLayout.EAST);

        // ── Campos ────────────────────────────────────────────────────────────
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(Color.WHITE);
        campos.setBorder(new EmptyBorder(20, 24, 10, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 0, 6, 16);
        gc.anchor = GridBagConstraints.WEST;

        String salario = vaga.getSalary() != null ? "R$ " + vaga.getSalary() : "Não informado";
        String[][] dados = {
            {"Área:",       vaga.getArea()},
            {"Modalidade:", vaga.getModality().name()},
            {"Local:",      vaga.getLocation()},
            {"Salário:",    salario},
            {"Status:",     vaga.getStatus().name()},
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

        JButton fecharBtn = ButtonFactory.secondary("Fechar");
        fecharBtn.addActionListener(e -> dispose());

        rodape.add(fecharBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(campos, BorderLayout.CENTER);
        root.add(rodape, BorderLayout.SOUTH);
        add(root);
    }
}
