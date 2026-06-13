package com.portal.gui.students;

import com.portal.model.Student;
import com.portal.service.StudentService;
import com.portal.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class StudentListPanel extends JPanel {

    private final StudentService service = new StudentService();

    private JTable tabela;
    private StudentTableModel model;
    private JTextField campoBusca;
    private JButton editarBtn;
    private JButton toggleBtn;

    public StudentListPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        build();
        carregar();
    }

    private void build() {
        // ── Topo ─────────────────────────────────────────────────────────────
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Color.WHITE);
        topo.setBorder(new EmptyBorder(16, 20, 8, 20));

        JLabel titulo = new JLabel("Gestão de Alunos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(0x1A237E));

        JPanel botoesDir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoesDir.setBackground(Color.WHITE);

        JButton novoBtn     = criarBotaoPrimario("+ Novo Aluno");
        JButton importarBtn = criarBotaoSecundario("⬆  Importar .txt");
        JButton atualizarBtn = criarBotaoSecundario("↻  Atualizar");

        novoBtn.addActionListener(e -> abrirFormulario(null));
        importarBtn.addActionListener(e -> abrirImportacao());
        atualizarBtn.addActionListener(e -> buscar());

        botoesDir.add(atualizarBtn);
        botoesDir.add(importarBtn);
        botoesDir.add(novoBtn);

        topo.add(titulo,    BorderLayout.WEST);
        topo.add(botoesDir, BorderLayout.EAST);

        // ── Barra de busca ───────────────────────────────────────────────────
        JPanel barraBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        barraBusca.setBackground(new Color(0xF5F5F5));
        barraBusca.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)));

        campoBusca = new JTextField(24);
        campoBusca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoBusca.setToolTipText("Buscar por nome ou RA");
        campoBusca.addActionListener(e -> buscar());

        JButton buscarBtn = criarBotaoSecundario("Buscar");
        buscarBtn.addActionListener(e -> buscar());

        JButton limparBtn = new JButton("Limpar");
        limparBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        limparBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        limparBtn.addActionListener(e -> { campoBusca.setText(""); carregar(); });

        barraBusca.add(new JLabel("Buscar:"));
        barraBusca.add(campoBusca);
        barraBusca.add(buscarBtn);
        barraBusca.add(limparBtn);

        JPanel topoCompleto = new JPanel(new BorderLayout());
        topoCompleto.setBackground(Color.WHITE);
        topoCompleto.add(topo,      BorderLayout.NORTH);
        topoCompleto.add(barraBusca, BorderLayout.SOUTH);

        // ── Tabela ───────────────────────────────────────────────────────────
        model = new StudentTableModel();
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
        tabela.getColumnModel().getColumn(1).setCellRenderer(center);  // RA
        tabela.getColumnModel().getColumn(3).setCellRenderer(new AptidaoRenderer());

        tabela.getColumnModel().getColumn(0).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(230);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(90);

        tabela.getSelectionModel().addListSelectionListener(e -> atualizarBotoes());
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) abrirFormulario(getAlunoSelecionado());
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        scroll.getViewport().setBackground(Color.WHITE);

        // ── Ações ─────────────────────────────────────────────────────────
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        acoes.setBackground(new Color(0xF5F5F5));
        acoes.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xE0E0E0)));

        editarBtn  = new JButton("Editar");
        toggleBtn  = new JButton("Marcar Inapto");
        estilizarBotaoSecundario(editarBtn);
        estilizarBotaoSecundario(toggleBtn);
        editarBtn.setEnabled(false);
        toggleBtn.setEnabled(false);

        editarBtn.addActionListener(e -> abrirFormulario(getAlunoSelecionado()));
        toggleBtn.addActionListener(e -> toggleEligivel());

        acoes.add(editarBtn);
        acoes.add(toggleBtn);

        add(topoCompleto, BorderLayout.NORTH);
        add(scroll,       BorderLayout.CENTER);
        add(acoes,        BorderLayout.SOUTH);
    }

    private void carregar() {
        model.setAlunos(service.listar());
        atualizarBotoes();
    }

    private void buscar() {
        String termo = campoBusca.getText().trim();
        model.setAlunos(service.buscar(termo));
        atualizarBotoes();
    }

    private void abrirFormulario(Student aluno) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        StudentFormDialog dialog = new StudentFormDialog(parent, aluno, service);
        dialog.setVisible(true);
        if (dialog.isSaved()) carregar();
    }

    private void abrirImportacao() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        StudentImportDialog dialog = new StudentImportDialog(parent, service);
        dialog.setVisible(true);
        carregar();
    }

    private void toggleEligivel() {
        Student aluno = getAlunoSelecionado();
        if (aluno == null) return;
        String acao = aluno.isEligible() ? "marcar como INAPTO" : "marcar como APTO";
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja " + acao + " o aluno \"" + aluno.getName() + "\"?",
            "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            service.toggleEligivel(aluno);
            carregar();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarBotoes() {
        Student s = getAlunoSelecionado();
        editarBtn.setEnabled(s != null);
        toggleBtn.setEnabled(s != null);
        if (s != null) toggleBtn.setText(s.isEligible() ? "Marcar Inapto" : "Marcar Apto");
    }

    private Student getAlunoSelecionado() {
        int row = tabela.getSelectedRow();
        return row < 0 ? null : model.getAluno(row);
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(new Color(0x1565C0));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton criarBotaoSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0x1565C0));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void estilizarBotaoSecundario(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // ── TableModel ────────────────────────────────────────────────────────────

    private static class StudentTableModel extends AbstractTableModel {
        private final String[] COLS = {"Nome", "RA", "E-mail", "Aptidão"};
        private List<Student> alunos = List.of();

        void setAlunos(List<Student> alunos) { this.alunos = alunos; fireTableDataChanged(); }
        Student getAluno(int row) { return alunos.get(row); }

        @Override public int getRowCount()    { return alunos.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int col) { return COLS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Student s = alunos.get(row);
            return switch (col) {
                case 0 -> s.getName();
                case 1 -> s.getRa();
                case 2 -> s.getEmail() != null ? s.getEmail() : "—";
                case 3 -> s.isEligible() ? "Apto" : "Inapto";
                default -> "";
            };
        }
    }

    // ── Renderer de aptidão ───────────────────────────────────────────────────

    private static class AptidaoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            if (!isSelected) {
                if ("Apto".equals(value)) {
                    setBackground(new Color(0xE8F5E9)); setForeground(new Color(0x2E7D32));
                } else {
                    setBackground(new Color(0xFFEBEE)); setForeground(new Color(0xC62828));
                }
            }
            return this;
        }
    }
}
