package com.portal.gui.applications;

import com.portal.dao.ApplicationDAO;
import com.portal.model.Application;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationListPanel extends JPanel {

    private final ApplicationDAO dao = new ApplicationDAO();

    private JTable tabela;
    private ApplicationTableModel model;
    private JComboBox<String> filtroStatus;
    private JButton detalhesBtn;
    private List<Application> todasCandidaturas = List.of();

    public ApplicationListPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        build();
        carregar();
    }

    private void build() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Color.WHITE);
        topo.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel titulo = new JLabel("Candidaturas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(0x1A237E));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controles.setBackground(Color.WHITE);

        JLabel lblFiltro = new JLabel("Status:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        filtroStatus = new JComboBox<>(
            new String[]{"Todos", "PENDING", "ANALYSING", "APPROVED", "REJECTED", "CANCELLED"});
        filtroStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filtroStatus.setPreferredSize(new Dimension(130, 30));
        filtroStatus.addActionListener(e -> filtrar());

        JButton atualizarBtn = new JButton("↻  Atualizar");
        atualizarBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        atualizarBtn.setForeground(Color.WHITE);
        atualizarBtn.setBackground(new Color(0x1565C0));
        atualizarBtn.setFocusPainted(false);
        atualizarBtn.setBorderPainted(false);
        atualizarBtn.setOpaque(true);
        atualizarBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        atualizarBtn.addActionListener(e -> carregar());

        controles.add(lblFiltro);
        controles.add(filtroStatus);
        controles.add(atualizarBtn);

        topo.add(titulo, BorderLayout.WEST);
        topo.add(controles, BorderLayout.EAST);

        model = new ApplicationTableModel();
        tabela = new JTable(model);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(32);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(0xFBE9E7));
        tabela.setGridColor(new Color(0xE0E0E0));
        tabela.setShowVerticalLines(false);

        tabela.getColumnModel().getColumn(2).setCellRenderer(new StatusRenderer());

        tabela.getColumnModel().getColumn(0).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(240);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(120);

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

        detalhesBtn = new JButton("Ver Detalhes");
        detalhesBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detalhesBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        detalhesBtn.setEnabled(false);
        detalhesBtn.addActionListener(e -> abrirDetalhes());

        acoes.add(detalhesBtn);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(acoes, BorderLayout.SOUTH);
    }

    private void carregar() {
        todasCandidaturas = dao.findAll();
        filtrar();
    }

    private void filtrar() {
        String filtro = (String) filtroStatus.getSelectedItem();
        List<Application> exibidas = "Todos".equals(filtro)
            ? todasCandidaturas
            : todasCandidaturas.stream()
                .filter(a -> a.getStatus().name().equals(filtro))
                .collect(Collectors.toList());
        model.setCandidaturas(exibidas);
        atualizarBotoes();
    }

    private void abrirDetalhes() {
        Application candidatura = getCandidaturaSelecionada();
        if (candidatura == null) return;
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        new ApplicationDetailDialog(parent, candidatura).setVisible(true);
    }

    private void atualizarBotoes() {
        detalhesBtn.setEnabled(getCandidaturaSelecionada() != null);
    }

    private Application getCandidaturaSelecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0) return null;
        return model.getCandidatura(row);
    }

    // ── TableModel ────────────────────────────────────────────────────────────

    private static class ApplicationTableModel extends AbstractTableModel {
        private final String[] COLS = {"Aluno", "Vaga", "Status"};
        private List<Application> candidaturas = List.of();

        void setCandidaturas(List<Application> candidaturas) {
            this.candidaturas = candidaturas;
            fireTableDataChanged();
        }

        Application getCandidatura(int row) { return candidaturas.get(row); }

        @Override public int getRowCount()    { return candidaturas.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int col) { return COLS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Application a = candidaturas.get(row);
            return switch (col) {
                case 0 -> a.getStudentName() != null ? a.getStudentName() : a.getStudentId();
                case 1 -> a.getJobTitle() != null ? a.getJobTitle() : a.getJobId();
                case 2 -> a.getStatus().name();
                default -> "";
            };
        }
    }

    // ── Renderer colorido para Status ─────────────────────────────────────────

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            if (!isSelected) {
                String s = value != null ? value.toString() : "";
                switch (s) {
                    case "PENDING"   -> { setBackground(new Color(0xFFF9C4)); setForeground(new Color(0xF57F17)); }
                    case "ANALYSING" -> { setBackground(new Color(0xE3F2FD)); setForeground(new Color(0x1565C0)); }
                    case "APPROVED"  -> { setBackground(new Color(0xE8F5E9)); setForeground(new Color(0x2E7D32)); }
                    case "REJECTED"  -> { setBackground(new Color(0xFFEBEE)); setForeground(new Color(0xC62828)); }
                    case "CANCELLED" -> { setBackground(new Color(0xF5F5F5)); setForeground(new Color(0x757575)); }
                    default          -> { setBackground(Color.WHITE);         setForeground(Color.BLACK); }
                }
            }
            return this;
        }
    }
}
