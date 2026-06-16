package com.portal.gui.dashboard;

import com.portal.gui.applications.ApplicationListPanel;
import com.portal.gui.companies.CompanyListPanel;
import com.portal.gui.course.CoursePanel;
import com.portal.gui.jobs.JobListPanel;
import com.portal.gui.reports.ReportPanel;
import com.portal.gui.students.StudentListPanel;
import com.portal.gui.users.UserListPanel;
import com.portal.util.ButtonFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {

    // Cores do menu lateral
    private static final Color NAV_BG       = new Color(0x1A237E); // fundo padrão
    private static final Color NAV_HOVER    = new Color(0x283593); // ao passar o mouse
    private static final Color NAV_SELECTED = new Color(0x0D47A1); // item selecionado
    private static final Color NAV_FG       = new Color(0xC5CAE9); // texto padrão

    private final JPanel contentArea = new JPanel(new CardLayout());

    private JButton inicioBtn;     // primeiro item, selecionado ao abrir
    private JButton navSelecionado; // item atualmente selecionado

    public DashboardFrame() {
        setTitle("Portal UniALFA — Back Office");
        setSize(1100, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));
        build();
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        root.add(buildTopBar(),  BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(contentArea,   BorderLayout.CENTER);

        // Registrar painéis
        contentArea.add(new DashboardHomePanel(),      "home");
        contentArea.add(new CompanyListPanel(),        "empresas");
        contentArea.add(new StudentListPanel(),        "alunos");
        contentArea.add(new CoursePanel(),             "cursos");
        contentArea.add(new JobListPanel(),            "vagas");
        contentArea.add(new ApplicationListPanel(),    "candidaturas");
        contentArea.add(new ReportPanel(),             "relatorios");
        contentArea.add(new UserListPanel(),           "usuarios");

        selecionar(inicioBtn, "home");
        add(root);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(0x1565C0));
        bar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titulo = new JLabel("Portal UniALFA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);

        JLabel userInfo = new JLabel("Back Office");
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userInfo.setForeground(new Color(0xBBDEFB));

        JButton sairBtn = new JButton("Sair");
        sairBtn.setBackground(new Color(0x0D47A1));
        sairBtn.setForeground(Color.WHITE);
        sairBtn.setFocusPainted(false);
        sairBtn.setBorderPainted(false);
        sairBtn.setOpaque(true);
        sairBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sairBtn.addActionListener(e -> {
            dispose();
            new com.portal.gui.login.LoginFrame().setVisible(true);
        });

        bar.add(titulo,   BorderLayout.WEST);
        bar.add(userInfo, BorderLayout.CENTER);
        bar.add(sairBtn,  BorderLayout.EAST);
        return bar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(0x1A237E));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(16, 0, 16, 0));

        inicioBtn = navItem("Início", "home");
        sidebar.add(inicioBtn);
        sidebar.add(navItem("Empresas",      "empresas"));
        sidebar.add(navItem("Alunos",        "alunos"));
        sidebar.add(navItem("Cursos",        "cursos"));
        sidebar.add(navItem("Vagas",         "vagas"));
        sidebar.add(navItem("Candidaturas",  "candidaturas"));
        sidebar.add(navItem("Relatórios",    "relatorios"));
        sidebar.add(navItem("Usuários",      "usuarios"));

        return sidebar;
    }

    private JButton navItem(String label, String card) {
        JButton btn = new JButton(label);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(NAV_FG);
        btn.setBackground(NAV_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 20, 0, 0));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addActionListener(e -> selecionar(btn, card));

        // Hover: realça o item sob o mouse (sem mexer no que já está selecionado)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != navSelecionado) btn.setBackground(NAV_HOVER);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != navSelecionado) btn.setBackground(NAV_BG);
            }
        });
        return btn;
    }

    /** Marca o item como selecionado (faixa azul + fundo destacado) e abre o painel. */
    private void selecionar(JButton btn, String card) {
        if (navSelecionado != null) {
            navSelecionado.setBackground(NAV_BG);
            navSelecionado.setForeground(NAV_FG);
            navSelecionado.setBorder(new EmptyBorder(0, 20, 0, 0));
        }
        navSelecionado = btn;
        btn.setBackground(NAV_SELECTED);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(0x64B5F6)), // faixa azul à esquerda
            new EmptyBorder(0, 16, 0, 0)));
        mostrar(card);
    }

    private void mostrar(String card) {
        ((CardLayout) contentArea.getLayout()).show(contentArea, card);
    }
}
