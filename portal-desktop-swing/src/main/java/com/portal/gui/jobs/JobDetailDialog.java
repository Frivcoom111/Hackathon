package com.portal.gui.jobs;

import com.portal.model.Job;
import com.portal.util.ButtonFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * JobDetailDialog: janela de DIÁLOGO que mostra os detalhes de uma vaga (somente leitura).
 *
 * "extends JDialog" indica que é uma janela secundária (um "pop-up") que abre por cima
 * da janela principal. Diferente de um JFrame, um JDialog depende de uma janela "pai".
 *
 * Esta tela apenas EXIBE os dados da vaga; não permite edição.
 */
public class JobDetailDialog extends JDialog {

    /**
     * @param parent a janela "pai" sobre a qual este diálogo será exibido.
     * @param vaga   a vaga cujos detalhes serão mostrados.
     */
    public JobDetailDialog(Frame parent, Job vaga) {
        // O "true" no final torna o diálogo MODAL: enquanto aberto, bloqueia a janela pai.
        super(parent, "Detalhes da Vaga", true);
        setSize(460, 380);
        setLocationRelativeTo(parent); // Centraliza em relação à janela pai.
        setResizable(false);
        build(vaga);
    }

    /** Monta o visual do diálogo a partir dos dados da vaga. */
    private void build(Job vaga) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Cabeçalho: título da vaga + status ────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x1565C0));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitulo = new JLabel(vaga.getTitle());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblStatus = new JLabel(vaga.getStatus().name()); // .name() = texto do enum.
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setForeground(new Color(0xBBDEFB));

        header.add(lblTitulo, BorderLayout.WEST);
        header.add(lblStatus, BorderLayout.EAST);

        // ── Campos: lista de "rótulo: valor" ──────────────────────────────────
        JPanel campos = new JPanel(new GridBagLayout());
        campos.setBackground(Color.WHITE);
        campos.setBorder(new EmptyBorder(20, 24, 10, 24));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 0, 6, 16);
        gc.anchor = GridBagConstraints.WEST; // Alinha tudo à esquerda.

        // Trata o salário, que pode ser nulo.
        String salario = vaga.getSalary() != null ? "R$ " + vaga.getSalary() : "Não informado";
        // Matriz com os pares rótulo/valor a exibir. Facilita montar tudo num laço.
        String[][] dados = {
            {"Área:",       vaga.getArea()},
            {"Modalidade:", vaga.getModality().name()},
            {"Local:",      vaga.getLocation()},
            {"Salário:",    salario},
            {"Status:",     vaga.getStatus().name()},
        };

        // Para cada par, adiciona o rótulo (coluna 0) e o valor (coluna 1) em uma linha.
        for (int i = 0; i < dados.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            JLabel lbl = new JLabel(dados[i][0]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setForeground(new Color(0x555555));
            campos.add(lbl, gc);

            gc.gridx = 1; gc.weightx = 1;
            // Se o valor for nulo, mostra um traço "—" no lugar.
            JLabel val = new JLabel(dados[i][1] != null ? dados[i][1] : "—");
            val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            campos.add(val, gc);
        }

        // ── Rodapé: botão "Fechar" ────────────────────────────────────────────
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        rodape.setBackground(new Color(0xF5F5F5));
        rodape.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xE0E0E0)));

        JButton fecharBtn = ButtonFactory.secondary("Fechar");
        fecharBtn.addActionListener(e -> dispose()); // dispose() fecha o diálogo.

        rodape.add(fecharBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(campos, BorderLayout.CENTER);
        root.add(rodape, BorderLayout.SOUTH);
        add(root);
    }
}
