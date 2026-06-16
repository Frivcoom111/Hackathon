package com.portal.gui.applications;

import com.portal.model.Application;
import com.portal.util.ButtonFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ApplicationDetailDialog: diálogo (pop-up) que mostra os detalhes de uma candidatura.
 *
 * É só leitura: exibe o aluno, a vaga e o status. O status aparece como um "badge"
 * (etiqueta colorida), montado pelo método buildStatusBadge.
 */
public class ApplicationDetailDialog extends JDialog {

    /**
     * @param parent      janela pai sobre a qual o diálogo aparece.
     * @param candidatura a candidatura a ser detalhada.
     */
    public ApplicationDetailDialog(Frame parent, Application candidatura) {
        super(parent, "Detalhes da Candidatura", true); // true = modal (bloqueia a janela pai).
        setSize(440, 300);
        setLocationRelativeTo(parent);
        setResizable(false);
        build(candidatura);
    }

    /** Monta o visual do diálogo a partir dos dados da candidatura. */
    private void build(Application candidatura) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Cabeçalho: título + badge de status ───────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x1565C0));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitulo = new JLabel("Candidatura");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblStatus = buildStatusBadge(candidatura.getStatus().name()); // Etiqueta colorida.

        header.add(lblTitulo, BorderLayout.WEST);
        header.add(lblStatus, BorderLayout.EAST);

        // ── Campos ────────────────────────────────────────────────────────────
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(Color.WHITE);
        campos.setBorder(new EmptyBorder(20, 24, 10, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 0, 8, 16);
        gc.anchor = GridBagConstraints.WEST;

        // Prefere mostrar o NOME do aluno/título da vaga; se faltar, usa o ID como reserva.
        String aluno = candidatura.getStudentName() != null
            ? candidatura.getStudentName() : candidatura.getStudentId();
        String vaga  = candidatura.getJobTitle() != null
            ? candidatura.getJobTitle() : candidatura.getJobId();

        String[][] dados = {
            {"Aluno:",  aluno},
            {"Vaga:",   vaga},
            {"Status:", candidatura.getStatus().name()},
        };

        // Mesmo laço de "rótulo + valor" usado nos outros diálogos.
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

        // ── Rodapé: botão "Fechar" ────────────────────────────────────────────
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

    /**
     * Cria uma "etiqueta" (badge) colorida para o status, com fundo claro e texto na
     * cor correspondente ao significado do status.
     *
     * Usa "switch com seta" (->), uma forma moderna e enxuta de switch no Java.
     */
    private JLabel buildStatusBadge(String status) {
        JLabel lbl = new JLabel(status);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setOpaque(true); // Necessário para que a cor de fundo apareça.
        lbl.setBorder(new EmptyBorder(3, 10, 3, 10)); // Espaçamento interno da etiqueta.
        // Para cada status, define um par de cores (fundo claro + texto forte).
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
