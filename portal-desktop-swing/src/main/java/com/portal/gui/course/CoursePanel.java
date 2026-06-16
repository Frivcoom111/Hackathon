package com.portal.gui.course;

import com.portal.model.Course;
import com.portal.service.CourseService;
import com.portal.util.ButtonFactory;
import com.portal.util.StatusCellRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

public class CoursePanel extends JPanel {

    private final CourseService service = new CourseService();

    private JTable tabela;
    private CourseTableModel model;
    private JButton editarBtn;
    private JButton toggleBtn;

    public CoursePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        build();
        carregar();
    }

    private void build() {
        // Cabeçalho do painel
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Color.WHITE);
        topo.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel titulo = new JLabel("Cursos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(0x1A237E));

        JButton novoBtn = ButtonFactory.primary("+ Novo Curso");
        novoBtn.addActionListener(e -> abrirFormulario(null));

        JButton atualizarBtn = ButtonFactory.primary("Atualizar");
        atualizarBtn.addActionListener(e -> carregar());

        JPanel botoesDir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoesDir.setBackground(Color.WHITE);
        botoesDir.add(atualizarBtn);
        botoesDir.add(novoBtn);

        topo.add(titulo, BorderLayout.WEST);
        topo.add(botoesDir, BorderLayout.EAST);

        // Tabela
        model = new CourseTableModel();
        tabela = new JTable(model);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(32);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(0xE3F2FD));
        tabela.setGridColor(new Color(0xE0E0E0));
        tabela.setShowVerticalLines(false);

        // Centralizar colunas Código e Semestres
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(1).setCellRenderer(center);
        tabela.getColumnModel().getColumn(2).setCellRenderer(center);
        // Status com cor (Ativo = verde, Inativo = vermelho) — mesmo padrão das outras telas
        tabela.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        tabela.getColumnModel().getColumn(0).setPreferredWidth(260);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(90);

        tabela.getSelectionModel().addListSelectionListener(e -> atualizarBotoes());

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        scroll.getViewport().setBackground(Color.WHITE);

        // Barra de ações
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        acoes.setBackground(new Color(0xF5F5F5));
        acoes.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xE0E0E0)));

        editarBtn = ButtonFactory.primary("Editar");
        toggleBtn = ButtonFactory.primary("Desativar");
        editarBtn.setEnabled(false);
        toggleBtn.setEnabled(false);

        editarBtn.addActionListener(e -> abrirFormulario(getCursoSelecionado()));
        toggleBtn.addActionListener(e -> toggleAtivo());

        acoes.add(editarBtn);
        acoes.add(toggleBtn);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(acoes, BorderLayout.SOUTH);
    }

    private void carregar() {
        List<Course> cursos = service.listar();
        model.setCursos(cursos);
        atualizarBotoes();
    }

    private void abrirFormulario(Course curso) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        CourseFormDialog dialog = new CourseFormDialog(parent, curso);
        dialog.setVisible(true);
        if (dialog.isSaved()) carregar();
    }

    private void toggleAtivo() {
        Course curso = getCursoSelecionado();
        if (curso == null) return;
        String acao = curso.isActive() ? "desativar" : "reativar";
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja " + acao + " o curso \"" + curso.getName() + "\"?",
            "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            service.toggleAtivo(curso);
            carregar();
        }
    }

    private void atualizarBotoes() {
        Course selecionado = getCursoSelecionado();
        boolean temSelecao = selecionado != null;
        editarBtn.setEnabled(temSelecao);
        toggleBtn.setEnabled(temSelecao);
        if (temSelecao) {
            toggleBtn.setText(selecionado.isActive() ? "Desativar" : "Reativar");
        }
    }

    private Course getCursoSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) return null;
        return model.getCurso(row);
    }

    // ── TableModel ────────────────────────────────────────────────────────────

    private static class CourseTableModel extends AbstractTableModel {
        private final String[] COLS = {"Nome", "Código", "Semestres", "Status"};
        private List<Course> cursos = List.of();

        void setCursos(List<Course> cursos) {
            this.cursos = cursos;
            fireTableDataChanged();
        }

        Course getCurso(int row) { return cursos.get(row); }

        @Override public int getRowCount()    { return cursos.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int col) { return COLS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Course c = cursos.get(row);
            return switch (col) {
                case 0 -> c.getName();
                case 1 -> c.getCode() != null ? c.getCode() : "—";
                case 2 -> c.getPeriods() + "º sem.";
                case 3 -> c.isActive() ? "Ativo" : "Inativo";
                default -> "";
            };
        }
    }
}
