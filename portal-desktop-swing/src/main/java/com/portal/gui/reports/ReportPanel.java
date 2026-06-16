package com.portal.gui.reports;

import com.portal.service.ReportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * ReportPanel: painel de RELATÓRIOS. Permite escolher uma pasta de destino e gerar
 * arquivos .txt com empresas, alunos, vagas ou candidaturas.
 *
 * Fluxo de uso: (1) o usuário escolhe a pasta; (2) clica em "Gerar .txt" no card
 * desejado; (3) o ReportService cria o arquivo e o painel oferece abrir a pasta.
 */
public class ReportPanel extends JPanel {

    private final ReportService service = new ReportService(); // Gera os relatórios.

    private JLabel pastaLabel;          // Mostra a pasta escolhida.
    private String pastaSelecionada;    // Caminho da pasta de destino (null até escolher).
    private JLabel statusLabel;         // Mostra o resultado da última geração (sucesso/erro).

    public ReportPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        build();
    }

    private void build() {
        // ── Topo: título ──────────────────────────────────────────────────────
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Color.WHITE);
        topo.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel titulo = new JLabel("Relatórios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(0x1A237E));

        topo.add(titulo, BorderLayout.WEST);

        // ── Seleção da pasta de destino ───────────────────────────────────────
        JPanel painelPasta = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelPasta.setBackground(new Color(0xF5F5F5));
        painelPasta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)),
            new EmptyBorder(8, 12, 8, 12)));

        JButton escolherBtn = new JButton("Escolher pasta de destino");
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

        // ── Cards de relatórios: 2x2 (um para cada tipo) ──────────────────────
        JPanel cards = new JPanel(new GridLayout(2, 2, 16, 16));
        cards.setBackground(Color.WHITE);
        cards.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Cada card recebe título, descrição e a ação a executar ao clicar em "Gerar .txt".
        cards.add(buildCard(
            "Empresas Cadastradas",
            "Lista todas as empresas com CNPJ, status e telefone.",
            e -> gerar("empresas")));

        cards.add(buildCard(
            "Alunos Cadastrados",
            "Lista todos os alunos com RA, CPF e aptidão para estágio.",
            e -> gerar("alunos")));

        cards.add(buildCard(
            "Vagas Disponíveis",
            "Lista todas as vagas ativas com área, modalidade e salário.",
            e -> gerar("vagas")));

        cards.add(buildCard(
            "Candidaturas",
            "Lista todas as candidaturas com nome do aluno, vaga e status.",
            e -> gerar("candidaturas")));

        // ── Rodapé: mensagem de status da última geração ──────────────────────
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

    /**
     * Cria um "card" de relatório com título, descrição e botão "Gerar .txt".
     * @param acao o que acontece ao clicar no botão (passado de fora, via lambda).
     */
    private JPanel buildCard(String titulo, String descricao,
                             java.awt.event.ActionListener acao) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0)),
            new EmptyBorder(16, 16, 16, 16)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(new Color(0x1565C0));

        // O "<html>" permite que o texto da descrição quebre em várias linhas dentro do card.
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

    /**
     * Abre uma janela para o usuário escolher a pasta de destino dos relatórios.
     * JFileChooser configurado para selecionar apenas PASTAS (não arquivos).
     */
    private void escolherPasta() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Escolher pasta para salvar relatórios");
        chooser.setApproveButtonText("Selecionar");

        // Se já houve uma pasta escolhida antes, reabre a partir dela.
        if (pastaSelecionada != null) {
            chooser.setCurrentDirectory(new File(pastaSelecionada));
        }

        int result = chooser.showOpenDialog(this);
        // Se o usuário confirmou a escolha, guarda o caminho e atualiza o rótulo.
        if (result == JFileChooser.APPROVE_OPTION) {
            pastaSelecionada = chooser.getSelectedFile().getAbsolutePath();
            pastaLabel.setText(pastaSelecionada);
            pastaLabel.setForeground(Color.DARK_GRAY);
            pastaLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }
    }

    /**
     * Gera o relatório do tipo informado, salvando na pasta escolhida.
     * Exige que uma pasta tenha sido selecionada; ao final, oferece abrir a pasta.
     *
     * @param tipo um entre "empresas", "alunos", "vagas" ou "candidaturas".
     */
    private void gerar(String tipo) {
        // Sem pasta escolhida, avisa e não faz nada.
        if (pastaSelecionada == null) {
            JOptionPane.showMessageDialog(this,
                "Selecione uma pasta de destino antes de gerar o relatório.",
                "Pasta não selecionada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Escolhe o método do serviço conforme o tipo; cada um devolve o caminho gerado.
            String caminho = switch (tipo) {
                case "empresas"      -> service.gerarEmpresas(pastaSelecionada);
                case "alunos"        -> service.gerarAlunos(pastaSelecionada);
                case "vagas"         -> service.gerarVagas(pastaSelecionada);
                case "candidaturas"  -> service.gerarCandidaturas(pastaSelecionada);
                default -> throw new IllegalArgumentException("Tipo desconhecido: " + tipo);
            };

            // Mostra mensagem de sucesso (verde) no rodapé.
            statusLabel.setForeground(new Color(0x2E7D32));
            statusLabel.setText("✔  Relatório salvo em: " + caminho);

            // Pergunta se o usuário quer abrir a pasta no explorador de arquivos.
            int abrir = JOptionPane.showConfirmDialog(this,
                "Relatório gerado com sucesso!\n\n" + caminho + "\n\nDeseja abrir a pasta?",
                "Sucesso", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (abrir == JOptionPane.YES_OPTION) {
                // Desktop.open abre a pasta no gerenciador de arquivos do sistema.
                Desktop.getDesktop().open(new File(pastaSelecionada));
            }

        } catch (Exception ex) {
            // Em caso de erro, mostra mensagem em vermelho e um diálogo de erro.
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("✘  Erro: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório:\n" + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
