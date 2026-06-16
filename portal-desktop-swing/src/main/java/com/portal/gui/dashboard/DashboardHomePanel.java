package com.portal.gui.dashboard;

import com.portal.dao.DashboardDAO;
import com.portal.model.DashboardStats;
import com.portal.util.ButtonFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * DashboardHomePanel: o painel da TELA INICIAL ("Início") do back office.
 *
 * Mostra uma saudação e quatro "cards" (cartões) com os números-resumo do sistema:
 * empresas pendentes, vagas ativas, candidaturas abertas e alunos aptos. Um botão
 * "Atualizar" recarrega esses números a partir do banco.
 *
 * "extends JPanel" significa que esta classe É um painel (uma área de conteúdo que
 * fica dentro da janela principal, e não uma janela separada).
 */
public class DashboardHomePanel extends JPanel {

    private final DashboardDAO dao = new DashboardDAO(); // Busca as estatísticas no banco.

    // Rótulos que exibem os números. Ficam como atributos para poder atualizá-los depois.
    private JLabel lblEmpresasPendentes;
    private JLabel lblVagasAtivas;
    private JLabel lblCandidaturasAbertas;
    private JLabel lblAlunosAptos;

    /** Construtor: configura o painel, monta o visual e já carrega os números. */
    public DashboardHomePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        build();    // Monta os componentes visuais.
        carregar(); // Busca e exibe os números atuais.
    }

    /** Monta toda a parte visual do painel (topo, cards e rodapé). */
    private void build() {
        // ── Topo: saudação + botão "Atualizar" ───────────────────────────────
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(0xE3F2FD)); // Azul bem claro.
        topo.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel saudacao = new JLabel("Bem-vindo ao Back Office");
        saudacao.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saudacao.setForeground(new Color(0x1A237E));

        JLabel subtitulo = new JLabel("Portal de Estágios — UniALFA");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(0x555555));

        // Empilha saudação e subtítulo verticalmente.
        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false); // Deixa o fundo do painel "topo" aparecer.
        textos.add(saudacao);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JButton atualizarBtn = ButtonFactory.primary("Atualizar");
        atualizarBtn.addActionListener(e -> carregar()); // Recarrega os números ao clicar.

        topo.add(textos,       BorderLayout.WEST);
        topo.add(atualizarBtn, BorderLayout.EAST);

        // ── Seção dos cards (contadores) ──────────────────────────────────────
        JPanel secaoCards = new JPanel(new BorderLayout());
        secaoCards.setBackground(Color.WHITE);
        secaoCards.setBorder(new EmptyBorder(28, 28, 0, 28));

        JLabel lblSecao = new JLabel("Visão Geral");
        lblSecao.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSecao.setForeground(new Color(0x555555));
        lblSecao.setBorder(new EmptyBorder(0, 0, 14, 0));

        // GridLayout(1, 4): organiza os cards em 1 linha e 4 colunas, com 16px de espaço entre eles.
        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 0));
        grid.setBackground(Color.WHITE);

        // Inicia os rótulos com "–"; os números reais entram no método carregar().
        lblEmpresasPendentes   = new JLabel("–");
        lblVagasAtivas         = new JLabel("–");
        lblCandidaturasAbertas = new JLabel("–");
        lblAlunosAptos         = new JLabel("–");

        // Cores usadas nos cards.
        Color bgCard     = new Color(0xE3F2FD);
        Color textCard   = new Color(0x1565C0);
        Color accentCard = new Color(0x1E88E5);

        // Cria um card para cada indicador.
        grid.add(buildCard("Empresas Pendentes",   lblEmpresasPendentes,   bgCard, textCard, accentCard));
        grid.add(buildCard("Vagas Ativas",         lblVagasAtivas,         bgCard, textCard, accentCard));
        grid.add(buildCard("Candidaturas Abertas", lblCandidaturasAbertas, bgCard, textCard, accentCard));
        grid.add(buildCard("Alunos Aptos",         lblAlunosAptos,         bgCard, textCard, accentCard));

        secaoCards.add(lblSecao, BorderLayout.NORTH);
        secaoCards.add(grid,     BorderLayout.CENTER);

        // ── Rodapé com uma dica de uso ────────────────────────────────────────
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rodape.setBackground(Color.WHITE);
        rodape.setBorder(new EmptyBorder(24, 0, 0, 0));

        JLabel dica = new JLabel("Use o menu lateral para navegar entre os módulos.");
        dica.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        dica.setForeground(Color.GRAY);
        rodape.add(dica);

        // Agrupa a seção de cards e o rodapé no centro da tela.
        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Color.WHITE);
        centro.add(secaoCards, BorderLayout.NORTH);
        centro.add(rodape,     BorderLayout.CENTER);

        add(topo,   BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
    }

    /**
     * Constrói um "card" (cartão) com um título em cima e um número grande embaixo.
     *
     * @param titulo      o texto do card (ex.: "Vagas Ativas").
     * @param lblNumero   o rótulo que exibirá o número (atualizado depois em carregar()).
     * @param bgColor     cor de fundo do card.
     * @param textColor   cor do texto/número.
     * @param accentColor cor da faixa de destaque à esquerda.
     */
    private JPanel buildCard(String titulo, JLabel lblNumero,
                             Color bgColor, Color textColor, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        // Faixa colorida de 4px à esquerda + espaçamento interno de 20px.
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
            new EmptyBorder(20, 20, 20, 20)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitulo.setForeground(textColor);

        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 40)); // Número bem grande e em destaque.
        lblNumero.setForeground(textColor);
        lblNumero.setHorizontalAlignment(SwingConstants.LEFT);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblNumero, BorderLayout.CENTER);
        return card;
    }

    /**
     * Busca as estatísticas no banco e atualiza os quatro números na tela.
     * É chamado ao abrir o painel e sempre que o usuário clica em "Atualizar".
     * String.valueOf converte o número (int) em texto para exibir no rótulo.
     */
    public void carregar() {
        DashboardStats stats = dao.getStats();
        lblEmpresasPendentes.setText(String.valueOf(stats.getEmpresasPendentes()));
        lblVagasAtivas.setText(String.valueOf(stats.getVagasAtivas()));
        lblCandidaturasAbertas.setText(String.valueOf(stats.getCandidaturasAbertas()));
        lblAlunosAptos.setText(String.valueOf(stats.getAlunosAptos()));
    }
}
