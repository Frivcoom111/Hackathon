package com.portal.gui.dashboard;

import com.portal.gui.applications.ApplicationListPanel;
import com.portal.gui.companies.CompanyListPanel;
import com.portal.gui.course.CoursePanel;
import com.portal.gui.jobs.JobListPanel;
import com.portal.gui.reports.ReportPanel;
import com.portal.gui.students.StudentListPanel;
import com.portal.util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private final JPanel contentArea = new JPanel(new CardLayout());

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
        contentArea.add(new CoursePanel(),            "cursos");
        contentArea.add(new CompanyListPanel(),        "empresas");
        contentArea.add(new StudentListPanel(),        "alunos");
        contentArea.add(new JobListPanel(),            "vagas");
        contentArea.add(new ApplicationListPanel(),   "candidaturas");
        contentArea.add(new ReportPanel(),             "relatorios");

        mostrar("empresas");
        add(root);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(0x1565C0));
        bar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titulo = new JLabel("Portal UniALFA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(Color.WHITE);

        String email = Session.getCurrentUser().getEmail();
        JLabel userInfo = new JLabel(email + "  |  ADMIN");
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
            Session.clear();
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

        sidebar.add(navItem("Empresas",      "empresas"));
        sidebar.add(navItem("Alunos",        "alunos"));
        sidebar.add(navItem("Cursos",        "cursos"));
        sidebar.add(navItem("Vagas",         "vagas"));
        sidebar.add(navItem("Candidaturas",  "candidaturas"));
        sidebar.add(navItem("Relatórios",    "relatorios"));

        return sidebar;
    }

    private JButton navItem(String label, String card) {
        JButton btn = new JButton(label);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(0xC5CAE9));
        btn.setBackground(new Color(0x1A237E));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(0, 20, 0, 0));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addActionListener(e -> mostrar(card));
        return btn;
    }

    private void mostrar(String card) {
        ((CardLayout) contentArea.getLayout()).show(contentArea, card);
    }
}
