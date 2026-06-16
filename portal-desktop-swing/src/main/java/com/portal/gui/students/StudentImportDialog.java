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

public class StudentImportDialog extends JDialog {

    private final StudentService service;

    private JLabel arquivoLabel;
    private File arquivoSelecionado;
    private PreviewTableModel previewModel;
    private JButton importarBtn;
    private JLabel statusLabel;

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

        // ── Cabeçalho ─────────────────────────────────────────────────────────
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

        // ── Preview da tabela ─────────────────────────────────────────────────
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

        // ── Rodapé ────────────────────────────────────────────────────────────
        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBackground(Color.WHITE);
        rodape.setBorder(new EmptyBorder(10, 0, 0, 0));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(0x1565C0));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setBackground(Color.WHITE);

        importarBtn = ButtonFactory.primary("Importar");
        importarBtn.setEnabled(false);
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

    private void escolherArquivo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos de texto (*.txt)", "txt"));
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        arquivoSelecionado = chooser.getSelectedFile();
        arquivoLabel.setText(arquivoSelecionado.getName());
        arquivoLabel.setForeground(Color.DARK_GRAY);

        try {
            List<Student> preview = com.portal.util.FileImportUtil.parseStudents(arquivoSelecionado.getAbsolutePath());
            previewModel.setAlunos(preview);
            statusLabel.setText(preview.size() + " aluno(s) encontrado(s) no arquivo.");
            importarBtn.setEnabled(!preview.isEmpty());
        } catch (Exception ex) {
            statusLabel.setText("Erro ao ler arquivo: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }

    private void importar() {
        if (arquivoSelecionado == null) return;
        try {
            service.importar(arquivoSelecionado.getAbsolutePath());
            statusLabel.setForeground(new Color(0x2E7D32));
            statusLabel.setText("Importação concluída com sucesso!");
            importarBtn.setEnabled(false);
            JOptionPane.showMessageDialog(this, "Alunos importados com sucesso!", "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (ServiceException ex) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Erro na importação.");
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro na Importação", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ── TableModel do preview ──────────────────────────────────────────────────

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
