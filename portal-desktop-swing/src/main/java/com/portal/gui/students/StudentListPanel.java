package com.portal.gui.students;

import com.portal.model.Student;
import com.portal.service.StudentService;
import com.portal.service.ServiceException;
import com.portal.util.ButtonFactory;
import com.portal.util.StatusCellRenderer;

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

        JButton novoBtn     = ButtonFactory.primary("+ Novo Aluno");
        JButton importarBtn = ButtonFactory.primary("Importar .txt");
        JButton atualizarBtn = ButtonFactory.primary("Atualizar");

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

        JButton buscarBtn = ButtonFactory.primary("Buscar");
        buscarBtn.addActionListener(e -> buscar());

        JButton limparBtn = ButtonFactory.primary("Limpar");
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
        tabela.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer()); // Aptidão

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

        editarBtn  = ButtonFactory.primary("Editar");
        toggleBtn  = ButtonFactory.primary("Marcar Inapto");
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
}
