package com.portal.gui.reports;

import com.portal.service.ReportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class ReportPanel extends JPanel {

    private final ReportService service = new ReportService();

    private JLabel pastaLabel;
    private String pastaSelecionada;
    private JLabel statusLabel;

    public ReportPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        build();
    }

    private void build() {
        // ── Topo ─────────────────────────────────────────────────────────────
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Color.WHITE);
        topo.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel titulo = new JLabel("Relatórios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(0x1A237E));

        topo.add(titulo, BorderLayout.WEST);

        // ── Seleção de pasta ──────────────────────────────────────────────────
        JPanel painelPasta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelPasta.setBackground(new Color(0xF5F5F5));
        painelPasta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)),
            new EmptyBorder(8, 12, 8, 12)));

        JButton escolherBtn = new JButton("📁  Escolher pasta de destino");
        escolherBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        escolherBtn.setBackground(new Color(0x1565C0));
        escolherBtn.setForeground(Color.WHITE);
        escolherBtn.setFocusPainted(false);
        escolherBtn.setBorderPainted(false);
        escolherBtn.setOpaque(true);
        escolherBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        escolherBtn.addActionListener(e -> escolherPasta());

        pastaLabel = new JLabel("Nenhuma pasta selecionada.");
        pastaLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        pastaLabel.setForeground(Color.GRAY);

        painelPasta.add(escolherBtn);
        painelPasta.add(pastaLabel);

        // ── Cards de relatórios ───────────────────────────────────────────────
        JPanel cards = new JPanel(new GridLayout(2, 2, 16, 16));
        cards.setBackground(Color.WHITE);
        cards.setBorder(new EmptyBorder(20, 20, 20, 20));

        cards.add(buildCard(
            "Empresas Cadastradas",
            "Lista todas as empresas com CNPJ, status e telefone.",
            new Color(0x1565C0),
            e -> gerar("empresas")));

        cards.add(buildCard(
            "Alunos Cadastrados",
            "Lista todos os alunos com RA, CPF e aptidão para estágio.",
            new Color(0x2E7D32),
            e -> gerar("alunos")));

        cards.add(buildCard(
            "Vagas Disponíveis",
            "Lista todas as vagas ativas com área, modalidade e salário.",
            new Color(0x6A1B9A),
            e -> gerar("vagas")));

        cards.add(buildCard(
            "Candidaturas",
            "Lista todas as candidaturas com nome do aluno, vaga e status.",
            new Color(0xE65100),
            e -> gerar("candidaturas")));

        // ── Status ────────────────────────────────────────────────────────────
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        rodape.setBackground(new Color(0xF5F5F5));
        rodape.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xE0E0E0)));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        rodape.add(statusLabel);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Color.WHITE);
        centro.add(painelPasta, BorderLayout.NORTH);
        centro.add(cards,       BorderLayout.CENTER);

        add(topo,   BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private JPanel buildCard(String titulo, String descricao, Color cor,
                             java.awt.event.ActionListener acao) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0)),
            new EmptyBorder(16, 16, 16, 16)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(0x1565C0));

        JLabel lblDesc = new JLabel("<html>" + descricao + "</html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(Color.DARK_GRAY);
        lblDesc.setBorder(new EmptyBorder(6, 0, 12, 0));

        JButton btn = new JButton("Gerar .txt");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(0x1565C0));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(acao);

        JPanel textos = new JPanel(new BorderLayout());
        textos.setBackground(Color.WHITE);
        textos.add(lblTitulo, BorderLayout.NORTH);
        textos.add(lblDesc,   BorderLayout.CENTER);

        card.add(textos, BorderLayout.CENTER);
        card.add(btn,    BorderLayout.SOUTH);
        return card;
    }

    private void escolherPasta() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Escolher pasta para salvar relatórios");
        chooser.setApproveButtonText("Selecionar");

        // Abre na última pasta usada, ou na home
        if (pastaSelecionada != null) {
            chooser.setCurrentDirectory(new File(pastaSelecionada));
        }

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            pastaSelecionada = chooser.getSelectedFile().getAbsolutePath();
            pastaLabel.setText(pastaSelecionada);
            pastaLabel.setForeground(Color.DARK_GRAY);
            pastaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
    }

    private void gerar(String tipo) {
        if (pastaSelecionada == null) {
            JOptionPane.showMessageDialog(this,
                "Selecione uma pasta de destino antes de gerar o relatório.",
                "Pasta não selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String caminho = switch (tipo) {
                case "empresas"      -> service.gerarEmpresas(pastaSelecionada);
                case "alunos"        -> service.gerarAlunos(pastaSelecionada);
                case "vagas"         -> service.gerarVagas(pastaSelecionada);
                case "candidaturas"  -> service.gerarCandidaturas(pastaSelecionada);
                default -> throw new IllegalArgumentException("Tipo desconhecido: " + tipo);
            };

            statusLabel.setForeground(new Color(0x2E7D32));
            statusLabel.setText("✔  Relatório salvo em: " + caminho);

            int abrir = JOptionPane.showConfirmDialog(this,
                "Relatório gerado com sucesso!\n\n" + caminho + "\n\nDeseja abrir a pasta?",
                "Sucesso", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (abrir == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(new File(pastaSelecionada));
            }

        } catch (Exception ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("✘  Erro: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório:\n" + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
