package com.portal.gui.students;

import com.portal.model.Student;
import com.portal.service.ServiceException;
import com.portal.service.StudentService;
import com.portal.util.ButtonFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * StudentImportDialog: diálogo para IMPORTAR alunos em massa a partir de um arquivo .txt.
 *
 * Fluxo: o usuário escolhe um arquivo no formato "nome;ra;cpf;email"; o sistema mostra
 * uma PRÉVIA (preview) dos alunos lidos numa tabela; ao confirmar, importa todos de uma vez.
 */
public class StudentImportDialog extends JDialog {

    private final StudentService service; // Serviço que lê o arquivo e salva os alunos.

    private JLabel arquivoLabel;            // Mostra o nome do arquivo escolhido.
    private File arquivoSelecionado;        // O arquivo escolhido (null até o usuário escolher).
    private PreviewTableModel previewModel; // Alimenta a tabela de prévia.
    private JButton importarBtn;
    private JLabel statusLabel;             // Mostra mensagens de status (lidos/importados/erros).

    public StudentImportDialog(Frame parent, StudentService service) {
        super(parent, "Importar Alunos via .txt", true);
        this.service = service;
        setSize(620, 460);
        setLocationRelativeTo(parent);
        setResizable(false);
        build();
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(16, 20, 12, 20));

        // ── Cabeçalho: título + dica do formato esperado ──────────────────────
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Color.WHITE);
        topo.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel titulo = new JLabel("Importar Alunos via .txt");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(0x1A237E));

        JLabel formato = new JLabel("Formato esperado: nome;ra;cpf;email");
        formato.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        formato.setForeground(Color.GRAY);

        topo.add(titulo,  BorderLayout.NORTH);
        topo.add(formato, BorderLayout.SOUTH);

        // ── Seleção de arquivo ────────────────────────────────────────────────
        JPanel selecao = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        selecao.setBackground(Color.WHITE);
        selecao.setBorder(new EmptyBorder(0, 0, 8, 0));

        arquivoLabel = new JLabel("Nenhum arquivo selecionado.");
        arquivoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        arquivoLabel.setForeground(Color.GRAY);

        JButton escolherBtn = ButtonFactory.primary("Escolher Arquivo...");
        escolherBtn.addActionListener(e -> escolherArquivo());

        selecao.add(escolherBtn);
        selecao.add(arquivoLabel);

        // ── Tabela de prévia (preview) ────────────────────────────────────────
        previewModel = new PreviewTableModel();
        JTable preview = new JTable(previewModel);
        preview.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        preview.setRowHeight(28);
        preview.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        preview.getTableHeader().setBackground(new Color(0xE3F2FD));
        preview.setGridColor(new Color(0xE0E0E0));
        preview.setShowVerticalLines(false);

        JScrollPane scroll = new JScrollPane(preview);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        scroll.getViewport().setBackground(Color.WHITE);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Color.WHITE);
        centro.add(selecao, BorderLayout.NORTH);
        centro.add(scroll,  BorderLayout.CENTER);

        // ── Rodapé: status + botões "Importar"/"Fechar" ───────────────────────
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(Color.WHITE);
        rodape.setBorder(new EmptyBorder(10, 0, 0, 0));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(0x1565C0));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setBackground(Color.WHITE);

        importarBtn = ButtonFactory.primary("Importar");
        importarBtn.setEnabled(false); // Só habilita depois que um arquivo válido é lido.
        importarBtn.addActionListener(e -> importar());

        JButton fecharBtn = ButtonFactory.secondary("Fechar");
        fecharBtn.addActionListener(e -> dispose());

        botoes.add(fecharBtn);
        botoes.add(importarBtn);

        rodape.add(statusLabel, BorderLayout.WEST);
        rodape.add(botoes,      BorderLayout.EAST);

        root.add(topo,   BorderLayout.NORTH);
        root.add(centro, BorderLayout.CENTER);
        root.add(rodape, BorderLayout.SOUTH);
        add(root);
    }

    /**
     * Abre o seletor de arquivos (.txt), lê o conteúdo e exibe a prévia dos alunos.
     * Ainda NÃO salva nada — só mostra o que será importado.
     */
    private void escolherArquivo() {
        JFileChooser chooser = new JFileChooser();
        // Filtra para mostrar apenas arquivos .txt.
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos de texto (*.txt)", "txt"));
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return; // Usuário cancelou.

        arquivoSelecionado = chooser.getSelectedFile();
        arquivoLabel.setText(arquivoSelecionado.getName());
        arquivoLabel.setForeground(Color.DARK_GRAY);

        try {
            // Lê e valida o arquivo, gerando a lista de prévia.
            List<Student> preview = com.portal.util.FileImportUtil.parseStudents(arquivoSelecionado.getAbsolutePath());
            previewModel.setAlunos(preview);
            statusLabel.setText(preview.size() + " aluno(s) encontrado(s) no arquivo.");
            importarBtn.setEnabled(!preview.isEmpty()); // Só permite importar se houver alunos.
        } catch (Exception ex) {
            statusLabel.setText("Erro ao ler arquivo: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }

    /**
     * Confirma a importação: chama o serviço para salvar os alunos do arquivo no banco.
     * Mostra mensagens de sucesso ou de erro conforme o resultado.
     */
    private void importar() {
        if (arquivoSelecionado == null) return;
        try {
            service.importar(arquivoSelecionado.getAbsolutePath());
            statusLabel.setForeground(new Color(0x2E7D32));
            statusLabel.setText("Importação concluída com sucesso!");
            importarBtn.setEnabled(false); // Evita importar o mesmo arquivo duas vezes.
            JOptionPane.showMessageDialog(this, "Alunos importados com sucesso!", "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (ServiceException ex) {
            // O serviço lança ServiceException quando, por exemplo, nenhum aluno pôde ser importado.
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Erro na importação.");
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro na Importação", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ── TableModel da prévia ────────────────────────────────────────────────────

    /** Alimenta a tabela de prévia com os alunos lidos do arquivo. */
    private static class PreviewTableModel extends AbstractTableModel {
        private final String[] COLS = {"Nome", "RA", "CPF", "E-mail"};
        private List<Student> alunos = List.of();

        void setAlunos(List<Student> alunos) { this.alunos = alunos; fireTableDataChanged(); }

        @Override public int getRowCount()    { return alunos.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int col) { return COLS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Student s = alunos.get(row);
            return switch (col) {
                case 0 -> s.getName();
                case 1 -> s.getRa();
                case 2 -> s.getCpf();
                case 3 -> s.getEmail() != null ? s.getEmail() : "—";
                default -> "";
            };
        }
    }
}
