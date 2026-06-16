package com.portal.gui.users;

import com.portal.dao.UserDAO;
import com.portal.model.User;
import com.portal.model.enums.Role;
import com.portal.util.ButtonFactory;
import com.portal.util.StatusCellRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class UserListPanel extends JPanel {

    private final UserDAO dao = new UserDAO();

    private JTable tabela;
    private UserTableModel model;
    private JComboBox<String> filtroPerfil;
    private JButton detalhesBtn;
    private List<User> todosUsuarios = List.of();

    public UserListPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        build();
        carregar();
    }

    private void build() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Color.WHITE);
        topo.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel titulo = new JLabel("Usuários");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(0x1A237E));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controles.setBackground(Color.WHITE);

        JLabel lblFiltro = new JLabel("Perfil:");
        lblFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        filtroPerfil = new JComboBox<>(new String[]{"Todos", "ADMIN", "STUDENT", "COMPANY"});
        filtroPerfil.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filtroPerfil.setPreferredSize(new Dimension(120, 30));
        filtroPerfil.addActionListener(e -> filtrar());

        JButton atualizarBtn = ButtonFactory.primary("Atualizar");
        atualizarBtn.addActionListener(e -> carregar());

        controles.add(lblFiltro);
        controles.add(filtroPerfil);
        controles.add(atualizarBtn);

        topo.add(titulo,    BorderLayout.WEST);
        topo.add(controles, BorderLayout.EAST);

        model  = new UserTableModel();
        tabela = new JTable(model);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(32);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(0xE3F2FD));
        tabela.setGridColor(new Color(0xE0E0E0));
        tabela.setShowVerticalLines(false);

        tabela.getColumnModel().getColumn(1).setCellRenderer(new StatusCellRenderer());
        tabela.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        tabela.getColumnModel().getColumn(0).setPreferredWidth(300);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);

        tabela.getSelectionModel().addListSelectionListener(e -> atualizarBotoes());
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
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

        add(topo,   BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(acoes,  BorderLayout.SOUTH);
    }

    private void carregar() {
        todosUsuarios = dao.findAll();
        filtrar();
    }

    private void filtrar() {
        String filtro = (String) filtroPerfil.getSelectedItem();
        List<User> exibidos = "Todos".equals(filtro)
            ? todosUsuarios
            : todosUsuarios.stream()
                .filter(u -> u.getRole().name().equals(filtro))
                .collect(Collectors.toList());
        model.setUsuarios(exibidos);
        atualizarBotoes();
    }

    private void abrirDetalhes() {
        User user = getUsuarioSelecionado();
        if (user == null) return;
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        new UserDetailDialog(parent, user).setVisible(true);
    }

    private void atualizarBotoes() {
        detalhesBtn.setEnabled(getUsuarioSelecionado() != null);
    }

    private User getUsuarioSelecionado() {
        int row = tabela.getSelectedRow();
        return row < 0 ? null : model.getUsuario(row);
    }

    // ── TableModel ────────────────────────────────────────────────────────────

    private static class UserTableModel extends AbstractTableModel {
        private final String[] COLS = {"E-mail", "Perfil", "Status"};
        private List<User> usuarios = List.of();

        void setUsuarios(List<User> usuarios) { this.usuarios = usuarios; fireTableDataChanged(); }
        User getUsuario(int row) { return usuarios.get(row); }

        @Override public int getRowCount()    { return usuarios.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int col) { return COLS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            User u = usuarios.get(row);
            return switch (col) {
                case 0 -> u.getEmail();
                case 1 -> rotuloRole(u.getRole());
                case 2 -> u.isActive() ? "Ativo" : "Inativo";
                default -> "";
            };
        }

        private String rotuloRole(Role r) {
            return switch (r) {
                case ADMIN   -> "Administrador";
                case STUDENT -> "Aluno";
                case COMPANY -> "Recrutador";
            };
        }
    }
}
