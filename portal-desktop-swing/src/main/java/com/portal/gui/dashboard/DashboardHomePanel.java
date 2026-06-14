package com.portal.gui.dashboard;

import com.portal.dao.DashboardDAO;
import com.portal.model.DashboardStats;
import com.portal.util.ButtonFactory;
import com.portal.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardHomePanel extends JPanel {

    private final DashboardDAO dao = new DashboardDAO();

    private JLabel lblEmpresasPendentes;
    private JLabel lblVagasAtivas;
    private JLabel lblCandidaturasAbertas;
    private JLabel lblAlunosAptos;

    public DashboardHomePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        build();
        carregar();
    }

    private void build() {
        // ── Topo ─────────────────────────────────────────────────────────────
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(0x1565C0));
        topo.setBorder(new EmptyBorder(24, 28, 24, 28));

        String email = Session.getCurrentUser() != null ? Session.getCurrentUser().getEmail() : "";
        JLabel saudacao = new JLabel("Bem-vindo ao Back Office, " + email);
        saudacao.setFont(new Font("Segoe UI", Font.BOLD, 18));
        saudacao.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Portal de Estágios — UniALFA");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(0xC5CAE9));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);
        textos.add(saudacao);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JButton atualizarBtn = ButtonFactory.primary("↻  Atualizar");
        atualizarBtn.addActionListener(e -> carregar());

        topo.add(textos,      BorderLayout.WEST);
        topo.add(atualizarBtn, BorderLayout.EAST);

        // ── Cards de contadores ───────────────────────────────────────────────
        JPanel secaoCards = new JPanel(new BorderLayout());
        secaoCards.setBackground(Color.WHITE);
        secaoCards.setBorder(new EmptyBorder(28, 28, 0, 28));

        JLabel lblSecao = new JLabel("Visão Geral");
        lblSecao.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSecao.setForeground(new Color(0x555555));
        lblSecao.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel grid = new JPanel(new GridLayout(1, 4, 16, 0));
        grid.setBackground(Color.WHITE);

        lblEmpresasPendentes   = new JLabel("–");
        lblVagasAtivas         = new JLabel("–");
        lblCandidaturasAbertas = new JLabel("–");
        lblAlunosAptos         = new JLabel("–");

        Color bgCard     = new Color(0xE3F2FD);
        Color textCard   = new Color(0x1565C0);
        Color accentCard = new Color(0x1E88E5);

        grid.add(buildCard("Empresas Pendentes",   lblEmpresasPendentes,   bgCard, textCard, accentCard));
        grid.add(buildCard("Vagas Ativas",         lblVagasAtivas,         bgCard, textCard, accentCard));
        grid.add(buildCard("Candidaturas Abertas", lblCandidaturasAbertas, bgCard, textCard, accentCard));
        grid.add(buildCard("Alunos Aptos",         lblAlunosAptos,         bgCard, textCard, accentCard));

        secaoCards.add(lblSecao, BorderLayout.NORTH);
        secaoCards.add(grid,     BorderLayout.CENTER);

        // ── Rodapé informativo ────────────────────────────────────────────────
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rodape.setBackground(Color.WHITE);
        rodape.setBorder(new EmptyBorder(24, 0, 0, 0));

        JLabel dica = new JLabel("Use o menu lateral para navegar entre os módulos.");
        dica.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        dica.setForeground(Color.GRAY);
        rodape.add(dica);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Color.WHITE);
        centro.add(secaoCards, BorderLayout.NORTH);
        centro.add(rodape,     BorderLayout.CENTER);

        add(topo,   BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
    }

    private JPanel buildCard(String titulo, JLabel lblNumero,
                             Color bgColor, Color textColor, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
            new EmptyBorder(20, 20, 20, 20)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitulo.setForeground(textColor);

        lblNumero.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblNumero.setForeground(textColor);
        lblNumero.setHorizontalAlignment(SwingConstants.LEFT);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblNumero, BorderLayout.CENTER);
        return card;
    }

    public void carregar() {
        DashboardStats stats = dao.getStats();
        lblEmpresasPendentes.setText(String.valueOf(stats.getEmpresasPendentes()));
        lblVagasAtivas.setText(String.valueOf(stats.getVagasAtivas()));
        lblCandidaturasAbertas.setText(String.valueOf(stats.getCandidaturasAbertas()));
        lblAlunosAptos.setText(String.valueOf(stats.getAlunosAptos()));
    }
}
