package com.portal.gui.course;

import com.portal.model.Course;
import com.portal.service.CourseService;
import com.portal.util.ButtonFactory;
import com.portal.util.StatusCellRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;        // Base para descrever os dados de uma tabela.
import javax.swing.table.DefaultTableCellRenderer;  // Renderer padrão (usado para centralizar texto).
import java.awt.*;
import java.util.List;

/**
 * CoursePanel: painel de listagem e gerenciamento de CURSOS.
 *
 * Mostra uma tabela com os cursos e botões para criar, editar e ativar/desativar.
 *
 * CONCEITO IMPORTANTE — JTable + TableModel (padrão MVC):
 *   - A JTable é só a parte VISUAL (a grade que aparece na tela).
 *   - Quem fornece os DADOS para a tabela é um "TableModel" (aqui, CourseTableModel).
 *   - A tabela pergunta ao model: "quantas linhas?", "quantas colunas?", "qual o valor
 *     da célula [linha][coluna]?". Assim, separamos os dados da aparência.
 */
public class CoursePanel extends JPanel {

    private final CourseService service = new CourseService(); // Regras de negócio dos cursos.

    private JTable tabela;            // A tabela visual.
    private CourseTableModel model;   // O "cérebro" que alimenta a tabela com os cursos.
    private JButton editarBtn;
    private JButton toggleBtn;        // Botão Ativar/Desativar (o texto muda conforme o curso).

    /** Construtor: monta a tela e já carrega os cursos. */
    public CoursePanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        build();
        carregar();
    }

    /** Monta a parte visual do painel (cabeçalho, tabela e barra de ações). */
    private void build() {
        // ── Cabeçalho: título + botões "Atualizar" e "Novo Curso" ─────────────
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Color.WHITE);
        topo.setBorder(new EmptyBorder(16, 20, 12, 20));

        JLabel titulo = new JLabel("Cursos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(0x1A237E));

        JButton novoBtn = ButtonFactory.primary("+ Novo Curso");
        novoBtn.addActionListener(e -> abrirFormulario(null)); // null = criar novo.

        JButton atualizarBtn = ButtonFactory.primary("Atualizar");
        atualizarBtn.addActionListener(e -> carregar());

        JPanel botoesDir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoesDir.setBackground(Color.WHITE);
        botoesDir.add(atualizarBtn);
        botoesDir.add(novoBtn);

        topo.add(titulo, BorderLayout.WEST);
        topo.add(botoesDir, BorderLayout.EAST);

        // ── Tabela de cursos ──────────────────────────────────────────────────
        model = new CourseTableModel();
        tabela = new JTable(model);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(32);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Só permite 1 linha selecionada.
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(0xE3F2FD));
        tabela.setGridColor(new Color(0xE0E0E0));
        tabela.setShowVerticalLines(false);

        // Renderer para centralizar o texto das colunas "Código" e "Semestres".
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(1).setCellRenderer(center);
        tabela.getColumnModel().getColumn(2).setCellRenderer(center);
        // Coluna "Status" com cor (Ativo = verde, Inativo = vermelho) — padrão das outras telas.
        tabela.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        // Define as larguras preferidas de cada coluna.
        tabela.getColumnModel().getColumn(0).setPreferredWidth(260);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(90);

        // Sempre que a seleção mudar, reavalia quais botões devem ficar habilitados.
        tabela.getSelectionModel().addListSelectionListener(e -> atualizarBotoes());

        // JScrollPane adiciona barras de rolagem quando há muitas linhas.
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        scroll.getViewport().setBackground(Color.WHITE);

        // ── Barra de ações (rodapé) ───────────────────────────────────────────
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        acoes.setBackground(new Color(0xF5F5F5));
        acoes.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xE0E0E0)));

        editarBtn = ButtonFactory.primary("Editar");
        toggleBtn = ButtonFactory.primary("Desativar");
        editarBtn.setEnabled(false); // Começam desabilitados: só ativam quando há seleção.
        toggleBtn.setEnabled(false);

        editarBtn.addActionListener(e -> abrirFormulario(getCursoSelecionado()));
        toggleBtn.addActionListener(e -> toggleAtivo());

        acoes.add(editarBtn);
        acoes.add(toggleBtn);

        add(topo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(acoes, BorderLayout.SOUTH);
    }

    /** Busca os cursos no serviço e atualiza a tabela. */
    private void carregar() {
        List<Course> cursos = service.listar();
        model.setCursos(cursos);
        atualizarBotoes();
    }

    /**
     * Abre o formulário de curso. Se "curso" for null, é criação; senão, é edição.
     * Após fechar, se algo foi salvo, recarrega a lista.
     */
    private void abrirFormulario(Course curso) {
        // Descobre a janela (Frame) que contém este painel, para usar como "pai" do diálogo.
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        CourseFormDialog dialog = new CourseFormDialog(parent, curso);
        dialog.setVisible(true);          // Diálogo modal: a execução pausa aqui até ele fechar.
        if (dialog.isSaved()) carregar(); // Se salvou, atualiza a tabela.
    }

    /** Ativa ou desativa o curso selecionado, pedindo confirmação antes. */
    private void toggleAtivo() {
        Course curso = getCursoSelecionado();
        if (curso == null) return;
        String acao = curso.isActive() ? "desativar" : "reativar";
        // Caixa de confirmação Sim/Não.
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja " + acao + " o curso \"" + curso.getName() + "\"?",
            "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            service.toggleAtivo(curso);
            carregar();
        }
    }

    /**
     * Habilita/desabilita os botões conforme houver ou não um curso selecionado,
     * e ajusta o texto do botão toggle (Desativar/Reativar) conforme o estado do curso.
     */
    private void atualizarBotoes() {
        Course selecionado = getCursoSelecionado();
        boolean temSelecao = selecionado != null;
        editarBtn.setEnabled(temSelecao);
        toggleBtn.setEnabled(temSelecao);
        if (temSelecao) {
            toggleBtn.setText(selecionado.isActive() ? "Desativar" : "Reativar");
        }
    }

    /**
     * Devolve o curso da linha selecionada na tabela, ou null se nada estiver selecionado.
     */
    private Course getCursoSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0) return null; // -1 significa "nenhuma linha selecionada".
        return model.getCurso(row);
    }

    // ── TableModel: a "ponte" entre a lista de cursos e a tabela visual ─────────

    /**
     * CourseTableModel: descreve para a JTable quais são as colunas e os valores das células.
     *
     * Estende AbstractTableModel e implementa os métodos essenciais que a tabela consulta:
     * número de linhas, número de colunas, nome das colunas e o valor de cada célula.
     */
    private static class CourseTableModel extends AbstractTableModel {
        private final String[] COLS = {"Nome", "Código", "Semestres", "Status"}; // Cabeçalhos.
        private List<Course> cursos = List.of(); // Lista atual de cursos (começa vazia).

        /** Troca a lista de cursos e avisa a tabela para se redesenhar. */
        void setCursos(List<Course> cursos) {
            this.cursos = cursos;
            fireTableDataChanged(); // Notifica a JTable de que os dados mudaram.
        }

        /** Devolve o curso de uma linha específica. */
        Course getCurso(int row) { return cursos.get(row); }

        @Override public int getRowCount()    { return cursos.size(); }   // Quantas linhas.
        @Override public int getColumnCount() { return COLS.length; }      // Quantas colunas.
        @Override public String getColumnName(int col) { return COLS[col]; } // Nome da coluna.

        /**
         * Devolve o valor que deve aparecer na célula [row][col].
         * A tabela chama este método para cada célula visível.
         */
        @Override
        public Object getValueAt(int row, int col) {
            Course c = cursos.get(row);
            return switch (col) {
                case 0 -> c.getName();
                case 1 -> c.getCode() != null ? c.getCode() : "—"; // Código pode ser nulo.
                case 2 -> c.getPeriods() + "º sem.";               // Ex.: "4º sem.".
                case 3 -> c.isActive() ? "Ativo" : "Inativo";       // boolean -> texto.
                default -> "";
            };
        }
    }
}
