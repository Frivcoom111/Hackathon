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

/**
 * StudentListPanel: painel principal de GESTÃO DE ALUNOS.
 *
 * É a tela mais completa de alunos: além de listar, permite buscar (por nome ou RA),
 * cadastrar, editar, importar em massa via .txt e alternar a aptidão (apto/inapto).
 *
 * Segue o mesmo padrão JTable + TableModel das demais telas.
 */
public class StudentListPanel extends JPanel {

    private final StudentService service = new StudentService(); // Regras de negócio dos alunos.

    private JTable tabela;
    private StudentTableModel model;
    private JTextField campoBusca;   // Campo de texto da busca.
    private JButton editarBtn;
    private JButton toggleBtn;       // Botão Apto/Inapto (texto muda conforme o aluno).

    public StudentListPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        build();
        carregar();
    }

    private void build() {
        // ── Topo: título + botões (Novo, Importar, Atualizar) ─────────────────
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

        novoBtn.addActionListener(e -> abrirFormulario(null)); // null = cadastro novo.
        importarBtn.addActionListener(e -> abrirImportacao());
        atualizarBtn.addActionListener(e -> buscar());

        botoesDir.add(atualizarBtn);
        botoesDir.add(importarBtn);
        botoesDir.add(novoBtn);

        topo.add(titulo,    BorderLayout.WEST);
        topo.add(botoesDir, BorderLayout.EAST);

        // ── Barra de busca ────────────────────────────────────────────────────
        JPanel barraBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        barraBusca.setBackground(new Color(0xF5F5F5));
        barraBusca.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xE0E0E0)));

        campoBusca = new JTextField(24);
        campoBusca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campoBusca.setToolTipText("Buscar por nome ou RA"); // Dica ao passar o mouse.
        campoBusca.addActionListener(e -> buscar()); // ENTER no campo também busca.

        JButton buscarBtn = ButtonFactory.primary("Buscar");
        buscarBtn.addActionListener(e -> buscar());

        JButton limparBtn = ButtonFactory.primary("Limpar");
        // Limpa o campo e recarrega a lista completa.
        limparBtn.addActionListener(e -> { campoBusca.setText(""); carregar(); });

        barraBusca.add(new JLabel("Buscar:"));
        barraBusca.add(campoBusca);
        barraBusca.add(buscarBtn);
        barraBusca.add(limparBtn);

        // Junta o título e a barra de busca em um único bloco de topo.
        JPanel topoCompleto = new JPanel(new BorderLayout());
        topoCompleto.setBackground(Color.WHITE);
        topoCompleto.add(topo,      BorderLayout.NORTH);
        topoCompleto.add(barraBusca, BorderLayout.SOUTH);

        // ── Tabela de alunos ──────────────────────────────────────────────────
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
        tabela.getColumnModel().getColumn(1).setCellRenderer(center);  // Coluna RA centralizada.
        tabela.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer()); // Aptidão colorida.

        tabela.getColumnModel().getColumn(0).setPreferredWidth(220);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(110);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(230);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(90);

        tabela.getSelectionModel().addListSelectionListener(e -> atualizarBotoes());
        // Duplo clique em um aluno abre o formulário de edição.
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) abrirFormulario(getAlunoSelecionado());
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        scroll.getViewport().setBackground(Color.WHITE);

        // ── Barra de ações (Editar / Apto-Inapto) ─────────────────────────────
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

    /** Carrega a lista completa de alunos. */
    private void carregar() {
        model.setAlunos(service.listar());
        atualizarBotoes();
    }

    /** Busca alunos pelo termo digitado (nome ou RA) e atualiza a tabela. */
    private void buscar() {
        String termo = campoBusca.getText().trim();
        model.setAlunos(service.buscar(termo));
        atualizarBotoes();
    }

    /**
     * Abre o formulário de aluno. null = novo; objeto = edição.
     * Após fechar, se algo foi salvo, recarrega a lista.
     */
    private void abrirFormulario(Student aluno) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        StudentFormDialog dialog = new StudentFormDialog(parent, aluno, service);
        dialog.setVisible(true);
        if (dialog.isSaved()) carregar();
    }

    /** Abre o diálogo de importação em massa e recarrega a lista ao fechar. */
    private void abrirImportacao() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        StudentImportDialog dialog = new StudentImportDialog(parent, service);
        dialog.setVisible(true);
        carregar();
    }

    /** Alterna a aptidão do aluno selecionado (apto/inapto), pedindo confirmação. */
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

    /**
     * Habilita/desabilita os botões conforme a seleção, e ajusta o texto do botão de
     * aptidão (Marcar Inapto / Marcar Apto) conforme o estado atual do aluno.
     */
    private void atualizarBotoes() {
        Student s = getAlunoSelecionado();
        editarBtn.setEnabled(s != null);
        toggleBtn.setEnabled(s != null);
        if (s != null) toggleBtn.setText(s.isEligible() ? "Marcar Inapto" : "Marcar Apto");
    }

    /** Devolve o aluno da linha selecionada, ou null se nada estiver selecionado. */
    private Student getAlunoSelecionado() {
        int row = tabela.getSelectedRow();
        return row < 0 ? null : model.getAluno(row);
    }

    // ── TableModel dos alunos ───────────────────────────────────────────────────

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
