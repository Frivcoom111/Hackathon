package com.portal.gui.dashboard;

// Importa todos os painéis (telas internas) que serão exibidos dentro do dashboard.
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

/**
 * DashboardFrame: a JANELA PRINCIPAL do sistema (back office), aberta após o login.
 *
 * Estrutura da tela:
 *   - Barra superior (topo) com título e botão "Sair";
 *   - Menu lateral (sidebar) à esquerda, com os módulos do sistema;
 *   - Área central de conteúdo, que troca de painel conforme o menu clicado.
 *
 * CONCEITO-CHAVE — CardLayout: a área central usa um "CardLayout", que funciona como
 * um baralho de cartas empilhadas: vários painéis ocupam o mesmo espaço, mas apenas um
 * fica visível por vez. Clicar no menu apenas mostra a "carta" correspondente.
 */
public class DashboardFrame extends JFrame {

    // Cores do menu lateral, definidas como constantes para reaproveitar e padronizar.
    private static final Color NAV_BG       = new Color(0x1A237E); // fundo padrão do item.
    private static final Color NAV_HOVER    = new Color(0x283593); // cor ao passar o mouse.
    private static final Color NAV_SELECTED = new Color(0x0D47A1); // cor do item selecionado.
    private static final Color NAV_FG       = new Color(0xC5CAE9); // cor do texto padrão.

    // Área central onde os painéis são empilhados e trocados (usa CardLayout).
    private final JPanel contentArea = new JPanel(new CardLayout());

    private JButton inicioBtn;      // Primeiro item do menu, selecionado ao abrir a tela.
    private JButton navSelecionado; // Guarda qual item do menu está atualmente selecionado.

    /** Construtor: configura a janela e dispara a montagem da interface. */
    public DashboardFrame() {
        setTitle("Portal UniALFA — Back Office");
        setSize(1100, 680);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela.
        setMinimumSize(new Dimension(800, 500)); // Tamanho mínimo ao redimensionar.
        build();
    }

    /** Monta a estrutura geral da tela (topo, menu lateral e área de conteúdo). */
    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        root.add(buildTopBar(),  BorderLayout.NORTH); // Barra superior no topo.
        root.add(buildSidebar(), BorderLayout.WEST);  // Menu lateral à esquerda.
        root.add(contentArea,    BorderLayout.CENTER); // Conteúdo no centro.

        // Registra cada painel na área central, associando-o a um nome (a "carta" do CardLayout).
        contentArea.add(new DashboardHomePanel(),      "home");
        contentArea.add(new CompanyListPanel(),        "empresas");
        contentArea.add(new StudentListPanel(),        "alunos");
        contentArea.add(new CoursePanel(),             "cursos");
        contentArea.add(new JobListPanel(),            "vagas");
        contentArea.add(new ApplicationListPanel(),    "candidaturas");
        contentArea.add(new ReportPanel(),             "relatorios");
        contentArea.add(new UserListPanel(),           "usuarios");

        selecionar(inicioBtn, "home"); // Começa exibindo a tela inicial ("home").
        add(root);
    }

    /** Constrói a barra superior (azul) com o título, o subtítulo e o botão "Sair". */
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
        // Ao clicar em "Sair": fecha o dashboard e volta para a tela de login.
        sairBtn.addActionListener(e -> {
            dispose();
            new com.portal.gui.login.LoginFrame().setVisible(true);
        });

        bar.add(titulo,   BorderLayout.WEST);
        bar.add(userInfo, BorderLayout.CENTER);
        bar.add(sairBtn,  BorderLayout.EAST);
        return bar;
    }

    /** Constrói o menu lateral (sidebar) com um botão para cada módulo do sistema. */
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        // BoxLayout vertical: empilha os botões do menu de cima para baixo.
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(0x1A237E));
        sidebar.setPreferredSize(new Dimension(200, 0)); // Largura fixa de 200px.
        sidebar.setBorder(new EmptyBorder(16, 0, 16, 0));

        inicioBtn = navItem("Início", "home"); // Guardamos a referência para selecioná-lo ao abrir.
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

    /**
     * Cria um item (botão) do menu lateral.
     *
     * @param label o texto exibido no menu.
     * @param card  o nome da "carta" do CardLayout que este item deve mostrar ao ser clicado.
     */
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
        btn.setHorizontalAlignment(SwingConstants.LEFT); // Texto alinhado à esquerda.
        // Ao clicar, seleciona este item e mostra o painel correspondente.
        btn.addActionListener(e -> selecionar(btn, card));

        // Efeito "hover": realça o item enquanto o mouse passa por cima
        // (sem alterar o item que já está selecionado).
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

    /**
     * Marca um item do menu como selecionado (faixa azul à esquerda + fundo destacado)
     * e exibe o painel correspondente na área central.
     */
    private void selecionar(JButton btn, String card) {
        // Primeiro, "desmarca" o item que estava selecionado antes (volta ao visual normal).
        if (navSelecionado != null) {
            navSelecionado.setBackground(NAV_BG);
            navSelecionado.setForeground(NAV_FG);
            navSelecionado.setBorder(new EmptyBorder(0, 20, 0, 0));
        }
        // Agora destaca o novo item selecionado.
        navSelecionado = btn;
        btn.setBackground(NAV_SELECTED);
        btn.setForeground(Color.WHITE);
        // Borda composta: faixa azul de 4px à esquerda + espaçamento interno.
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(0x64B5F6)),
            new EmptyBorder(0, 16, 0, 0)));
        mostrar(card); // Troca o painel exibido.
    }

    /** Mostra na área central a "carta" (painel) identificada pelo nome informado. */
    private void mostrar(String card) {
        ((CardLayout) contentArea.getLayout()).show(contentArea, card);
    }
}
