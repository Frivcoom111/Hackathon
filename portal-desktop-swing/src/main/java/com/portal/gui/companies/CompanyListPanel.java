package com.portal.gui.companies;

import com.portal.model.Company;
import com.portal.model.enums.CompanyStatus;
import com.portal.service.CompanyService;
import com.portal.util.ButtonFactory;
import com.portal.util.StatusCellRenderer;
import com.portal.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * CompanyListPanel: painel de GESTÃO DE EMPRESAS.
 *
 * Lista as empresas, permite filtrar por status e abrir os detalhes (onde é possível
 * aprovar, analisar ou bloquear a empresa). Diferente de outras telas, aqui o filtro
 * é feito direto no banco (chamando service.listarPorStatus), e não em memória.
 */
public class CompanyListPanel extends JPanel {

    private final CompanyService service = new CompanyService();

    private JTable tabela;
    private CompanyTableModel model;
    private JComboBox<String> filtroStatus;
    private JButton detalhesBtn;

    public CompanyListPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        build();
        carregar();
    }

    private void build() {
        // ── Topo: título + filtro de status + atualizar ───────────────────────
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Color.WHITE);
        topo.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel titulo = new JLabel("Gestão de Empresas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(0x1A237E));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controles.setBackground(Color.WHITE);

        JLabel lblFiltro = new JLabel("Filtrar:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        filtroStatus = new JComboBox<>(new String[]{"Todos", "PENDING", "ANALYSING", "APPROVED", "BLOCKED"});
        filtroStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filtroStatus.setPreferredSize(new Dimension(130, 30));
        filtroStatus.addActionListener(e -> carregar()); // Trocar o filtro recarrega do banco.

        JButton atualizarBtn = ButtonFactory.primary("Atualizar");
        atualizarBtn.addActionListener(e -> carregar());

        controles.add(lblFiltro);
        controles.add(filtroStatus);
        controles.add(atualizarBtn);

        topo.add(titulo, BorderLayout.WEST);
        topo.add(controles, BorderLayout.EAST);

        // ── Tabela de empresas ────────────────────────────────────────────────
        model = new CompanyTableModel();
        tabela = new JTable(model);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(32);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(0xE3F2FD));
        tabela.setGridColor(new Color(0xE0E0E0));
        tabela.setShowVerticalLines(false);

        // Centraliza CNPJ e Telefone; aplica cor na coluna Status.
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(1).setCellRenderer(center);
        tabela.getColumnModel().getColumn(2).setCellRenderer(center);
        tabela.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        tabela.getColumnModel().getColumn(0).setPreferredWidth(240);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(160);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(110);

        tabela.getSelectionModel().addListSelectionListener(e -> atualizarBotoes());
        // Duplo clique abre os detalhes/gerenciamento da empresa.
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) abrirDetalhes();
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        scroll.getViewport().setBackground(Color.WHITE);

        // ── Barra de ações ────────────────────────────────────────────────────
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        acoes.setBackground(new Color(0xF5F5F5));
        acoes.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xE0E0E0)));

        detalhesBtn = ButtonFactory.primary("Ver Detalhes / Gerenciar");
        detalhesBtn.setEnabled(false);
        detalhesBtn.addActionListener(e -> abrirDetalhes());

        acoes.add(detalhesBtn);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(acoes, BorderLayout.SOUTH);
    }

    /**
     * Carrega as empresas conforme o filtro selecionado.
     * Se for "Todos", lista todas; senão, consulta o banco já filtrando pelo status.
     */
    private void carregar() {
        String filtro = (String) filtroStatus.getSelectedItem();
        List<Company> lista;
        if ("Todos".equals(filtro)) {
            lista = service.listar();
        } else {
            // Converte o texto do filtro para o enum e busca só aquele status.
            lista = service.listarPorStatus(CompanyStatus.valueOf(filtro));
        }
        model.setEmpresas(lista);
        atualizarBotoes();
    }

    /**
     * Abre o diálogo de detalhes/gerenciamento da empresa selecionada.
     * Ao fechar, recarrega a lista (o status pode ter mudado lá dentro).
     */
    private void abrirDetalhes() {
        Company empresa = getEmpresaSelecionada();
        if (empresa == null) return;
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        CompanyDetailDialog dialog = new CompanyDetailDialog(parent, empresa, service);
        dialog.setVisible(true);
        carregar();
    }

    private void atualizarBotoes() {
        detalhesBtn.setEnabled(getEmpresaSelecionada() != null);
    }

    private Company getEmpresaSelecionada() {
        int row = tabela.getSelectedRow();
        if (row < 0) return null;
        return model.getEmpresa(row);
    }

    // ── TableModel das empresas ─────────────────────────────────────────────────

    private static class CompanyTableModel extends AbstractTableModel {
        private final String[] COLS = {"Nome", "CNPJ", "Telefone", "Status"};
        private List<Company> empresas = List.of();

        void setEmpresas(List<Company> empresas) {
            this.empresas = empresas;
            fireTableDataChanged();
        }

        Company getEmpresa(int row) { return empresas.get(row); }

        @Override public int getRowCount()    { return empresas.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int col) { return COLS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Company c = empresas.get(row);
            return switch (col) {
                case 0 -> c.getName();
                case 1 -> ValidationUtil.formatCnpj(c.getCnpj()); // CNPJ formatado para exibição.
                case 2 -> c.getPhone() != null ? ValidationUtil.formatPhone(c.getPhone()) : "—";
                case 3 -> c.getStatus().name();
                default -> "";
            };
        }
    }
}
