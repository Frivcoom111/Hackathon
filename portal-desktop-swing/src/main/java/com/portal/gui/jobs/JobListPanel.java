package com.portal.gui.jobs;

import com.portal.dao.JobDAO;
import com.portal.model.Job;
import com.portal.util.ButtonFactory;
import com.portal.util.StatusCellRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class JobListPanel extends JPanel {

    private final JobDAO dao = new JobDAO();

    private JTable tabela;
    private JobTableModel model;
    private JComboBox<String> filtroStatus;
    private JButton detalhesBtn;
    private List<Job> todasVagas = List.of();

    public JobListPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        build();
        carregar();
    }

    private void build() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Color.WHITE);
        topo.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel titulo = new JLabel("Vagas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(0x1A237E));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controles.setBackground(Color.WHITE);

        JLabel lblFiltro = new JLabel("Status:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        filtroStatus = new JComboBox<>(new String[]{"Todos", "ACTIVE", "PAUSED", "CLOSED"});
        filtroStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filtroStatus.setPreferredSize(new Dimension(120, 30));
        filtroStatus.addActionListener(e -> filtrar());

        JButton atualizarBtn = ButtonFactory.primary("Atualizar");
        atualizarBtn.addActionListener(e -> carregar());

        controles.add(lblFiltro);
        controles.add(filtroStatus);
        controles.add(atualizarBtn);

        topo.add(titulo, BorderLayout.WEST);
        topo.add(controles, BorderLayout.EAST);

        model = new JobTableModel();
        tabela = new JTable(model);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(32);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(0xE3F2FD));
        tabela.setGridColor(new Color(0xE0E0E0));
        tabela.setShowVerticalLines(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(2).setCellRenderer(center); // Modalidade
        tabela.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer()); // Status
        tabela.getColumnModel().getColumn(5).setCellRenderer(center); // Salário

        tabela.getColumnModel().getColumn(0).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(100);

        tabela.getSelectionModel().addListSelectionListener(e -> atualizarBotoes());
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) abrirDetalhes();
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        acoes.setBackground(new Color(0xF5F5F5));
        acoes.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xE0E0E0)));

        detalhesBtn = ButtonFactory.primary("Ver Detalhes");
        detalhesBtn.setEnabled(false);
        detalhesBtn.addActionListener(e -> abrirDetalhes());

        acoes.add(detalhesBtn);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(acoes, BorderLayout.SOUTH);
    }

    private void carregar() {
        todasVagas = dao.findAll();
        filtrar();
    }

    private void filtrar() {
        String filtro = (String) filtroStatus.getSelectedItem();
        List<Job> exibidas = "Todos".equals(filtro)
            ? todasVagas
            : todasVagas.stream()
                .filter(j -> j.getStatus().name().equals(filtro))
                .collect(Collectors.toList());
        model.setVagas(exibidas);
        atualizarBotoes();
    }

    private void abrirDetalhes() {
        Job vaga = getVagaSelecionada();
        if (vaga == null) return;
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        new JobDetailDialog(parent, vaga).setVisible(true);
    }

    private void atualizarBotoes() {
        detalhesBtn.setEnabled(getVagaSelecionada() != null);
    }

    private Job getVagaSelecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0) return null;
        return model.getVaga(row);
    }

    // ── TableModel ────────────────────────────────────────────────────────────

    private static class JobTableModel extends AbstractTableModel {
        private static final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        private final String[] COLS = {"Título", "Área", "Modalidade", "Local", "Status", "Salário"};
        private List<Job> vagas = List.of();

        void setVagas(List<Job> vagas) {
            this.vagas = vagas;
            fireTableDataChanged();
        }

        Job getVaga(int row) { return vagas.get(row); }

        @Override public int getRowCount()    { return vagas.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int col) { return COLS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Job j = vagas.get(row);
            return switch (col) {
                case 0 -> j.getTitle();
                case 1 -> j.getArea();
                case 2 -> j.getModality().name();
                case 3 -> j.getLocation();
                case 4 -> j.getStatus().name();
                case 5 -> j.getSalary() != null ? BRL.format(j.getSalary()) : "—";
                default -> "";
            };
        }
    }
}
