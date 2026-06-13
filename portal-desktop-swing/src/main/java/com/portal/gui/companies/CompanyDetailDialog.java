package com.portal.gui.companies;

import com.portal.dao.CompanyMemberDAO;
import com.portal.model.Company;
import com.portal.model.CompanyMember;
import com.portal.model.enums.CompanyStatus;
import com.portal.service.CompanyService;
import com.portal.service.ServiceException;
import com.portal.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class CompanyDetailDialog extends JDialog {

    private final Company company;
    private final CompanyService service;
    private final CompanyMemberDAO memberDAO = new CompanyMemberDAO();

    private JLabel statusLabel;
    private JButton analisarBtn;
    private JButton aprovarBtn;
    private JButton bloquearBtn;

    public CompanyDetailDialog(Frame parent, Company company, CompanyService service) {
        super(parent, "Detalhes da Empresa", true);
        this.company = company;
        this.service = service;
        setSize(620, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        build();
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(16, 20, 12, 20));

        root.add(buildHeader(),  BorderLayout.NORTH);
        root.add(buildTabs(),    BorderLayout.CENTER);
        root.add(buildAcoes(),   BorderLayout.SOUTH);

        add(root);
    }

    // ── Cabeçalho com nome e badge de status ──────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel nome = new JLabel(company.getName());
        nome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nome.setForeground(new Color(0x1A237E));

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        statusLabel.setOpaque(true);
        atualizarCorStatus();

        header.add(nome,        BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);
        return header;
    }

    // ── Abas: Dados / Usuários Vinculados ────────────────────────────────────

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.addTab("Dados da Empresa",      buildTabDados());
        tabs.addTab("Usuários Vinculados",   buildTabUsuarios());
        return tabs;
    }

    // ── Aba 1: Dados cadastrais ───────────────────────────────────────────────

    private JPanel buildTabDados() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 8, 8, 8));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 8, 5, 8);
        gc.anchor = GridBagConstraints.WEST;

        addCampo(panel, gc, "CNPJ:",     ValidationUtil.formatCnpj(company.getCnpj()), 0);
        addCampo(panel, gc, "Telefone:", company.getPhone() != null
                ? ValidationUtil.formatPhone(company.getPhone()) : "Não informado", 1);

        gc.gridx = 0; gc.gridy = 2;
        JLabel lblDesc = new JLabel("Descrição:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lblDesc, gc);

        gc.gridx = 1; gc.gridy = 2; gc.gridwidth = 2;
        gc.fill = GridBagConstraints.BOTH; gc.weightx = 1.0; gc.weighty = 1.0;
        String descTexto = company.getDescription() != null ? company.getDescription() : "Sem descrição.";
        JTextArea descArea = new JTextArea(descTexto);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setEditable(false);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setBackground(new Color(0xFAFAFA));
        descArea.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(340, 90));
        descScroll.setBorder(null);
        panel.add(descScroll, gc);

        return panel;
    }

    // ── Aba 2: Usuários vinculados (CompanyMember + User) ────────────────────

    private JPanel buildTabUsuarios() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 8, 8, 8));

        List<CompanyMember> membros = memberDAO.findByCompanyId(company.getId());

        MemberTableModel model = new MemberTableModel(membros);
        JTable tabela = new JTable(model);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(30);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(0xE3F2FD));
        tabela.setGridColor(new Color(0xE0E0E0));
        tabela.setShowVerticalLines(false);

        // Centralizar Perfil e Situação
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(2).setCellRenderer(center);
        tabela.getColumnModel().getColumn(3).setCellRenderer(new SituacaoRenderer());

        tabela.getColumnModel().getColumn(0).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        scroll.getViewport().setBackground(Color.WHITE);

        if (membros.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum usuário vinculado a esta empresa.", SwingConstants.CENTER);
            vazio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            vazio.setForeground(Color.GRAY);
            panel.add(vazio, BorderLayout.CENTER);
        } else {
            panel.add(scroll, BorderLayout.CENTER);
        }

        return panel;
    }

    // ── Botões de ação ────────────────────────────────────────────────────────

    private JPanel buildAcoes() {
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acoes.setBackground(Color.WHITE);
        acoes.setBorder(new EmptyBorder(12, 0, 0, 0));

        analisarBtn = criarBotao("Iniciar Análise", new Color(0x1565C0));
        aprovarBtn  = criarBotao("Aprovar",         new Color(0x2E7D32));
        bloquearBtn = criarBotao("Bloquear",        new Color(0xC62828));
        JButton fecharBtn = criarBotao("Fechar",    new Color(0x616161));

        analisarBtn.addActionListener(e -> executarAcao("analisar"));
        aprovarBtn.addActionListener(e -> executarAcao("aprovar"));
        bloquearBtn.addActionListener(e -> executarAcao("bloquear"));
        fecharBtn.addActionListener(e -> dispose());

        atualizarBotoes();

        acoes.add(analisarBtn);
        acoes.add(aprovarBtn);
        acoes.add(bloquearBtn);
        acoes.add(fecharBtn);
        return acoes;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void addCampo(JPanel panel, GridBagConstraints gc, String label, String valor, int row) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1; gc.fill = GridBagConstraints.NONE;
        gc.weightx = 0; gc.weighty = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, gc);

        gc.gridx = 1; gc.gridy = row; gc.gridwidth = 2;
        JLabel val = new JLabel(valor);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(val, gc);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void executarAcao(String acao) {
        String label = switch (acao) {
            case "analisar" -> "iniciar análise de";
            case "aprovar"  -> "aprovar";
            case "bloquear" -> "bloquear";
            default -> acao;
        };
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja " + label + " a empresa \"" + company.getName() + "\"?",
            "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            switch (acao) {
                case "analisar" -> service.analisar(company);
                case "aprovar"  -> service.aprovar(company);
                case "bloquear" -> service.bloquear(company);
            }
            atualizarCorStatus();
            atualizarBotoes();
            JOptionPane.showMessageDialog(this, "Status atualizado com sucesso.", "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarCorStatus() {
        statusLabel.setText(company.getStatus().name());
        switch (company.getStatus()) {
            case PENDING   -> { statusLabel.setBackground(new Color(0xFFF9C4)); statusLabel.setForeground(new Color(0xF57F17)); }
            case ANALYSING -> { statusLabel.setBackground(new Color(0xE3F2FD)); statusLabel.setForeground(new Color(0x1565C0)); }
            case APPROVED  -> { statusLabel.setBackground(new Color(0xE8F5E9)); statusLabel.setForeground(new Color(0x2E7D32)); }
            case BLOCKED   -> { statusLabel.setBackground(new Color(0xFFEBEE)); statusLabel.setForeground(new Color(0xC62828)); }
        }
    }

    private void atualizarBotoes() {
        CompanyStatus s = company.getStatus();
        analisarBtn.setEnabled(s == CompanyStatus.PENDING);
        aprovarBtn.setEnabled(s == CompanyStatus.ANALYSING || s == CompanyStatus.BLOCKED);
        bloquearBtn.setEnabled(s != CompanyStatus.BLOCKED);
    }

    // ── TableModel dos membros ────────────────────────────────────────────────

    private static class MemberTableModel extends AbstractTableModel {
        private final String[] COLS = {"Nome", "E-mail", "Perfil", "Situação"};
        private final List<CompanyMember> membros;

        MemberTableModel(List<CompanyMember> membros) { this.membros = membros; }

        @Override public int getRowCount()    { return membros.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int col) { return COLS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            CompanyMember m = membros.get(row);
            return switch (col) {
                case 0 -> m.getName();
                case 1 -> m.getEmail();
                case 2 -> m.getRole().name();
                case 3 -> m.isUserActive() ? "Ativo" : "Inativo";
                default -> "";
            };
        }
    }

    // ── Renderer colorido para Situação ───────────────────────────────────────

    private static class SituacaoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            if (!isSelected) {
                if ("Ativo".equals(value)) {
                    setBackground(new Color(0xE8F5E9)); setForeground(new Color(0x2E7D32));
                } else {
                    setBackground(new Color(0xFFEBEE)); setForeground(new Color(0xC62828));
                }
            }
            return this;
        }
    }
}
