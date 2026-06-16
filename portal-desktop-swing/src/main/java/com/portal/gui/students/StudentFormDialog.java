package com.portal.gui.students;

import com.portal.model.Address;
import com.portal.model.Student;
import com.portal.service.ServiceException;
import com.portal.service.StudentService;
import com.portal.util.ButtonFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StudentFormDialog extends JDialog {

    private final Student studentEdicao;
    private final StudentService service;
    private boolean saved = false;

    private JTextField nomeField;
    private JTextField raField;
    private JTextField cpfField;
    private JTextField emailField;
    private JTextField telefoneField;

    public StudentFormDialog(Frame parent, Student student, StudentService service) {
        super(parent, student == null ? "Novo Aluno" : "Editar Aluno", true);
        this.studentEdicao = student;
        this.service = service;
        setSize(460, 420);
        setLocationRelativeTo(parent);
        setResizable(false);
        build();
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Cabeçalho ─────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0x1565C0));
        header.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel titulo = new JLabel(studentEdicao == null ? "Cadastrar Aluno" : "Editar Aluno");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(Color.WHITE);
        header.add(titulo, BorderLayout.WEST);

        // ── Abas ──────────────────────────────────────────────────────────────
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        abas.addTab("Dados",    buildDadosTab());
        abas.addTab("Endereço", buildEnderecoTab());

        // ── Botões ────────────────────────────────────────────────────────────
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botoes.setBackground(new Color(0xF5F5F5));
        botoes.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0xE0E0E0)));

        JButton salvarBtn   = ButtonFactory.primary("Salvar");
        JButton cancelarBtn = ButtonFactory.secondary("Cancelar");
        salvarBtn.addActionListener(e -> salvar());
        cancelarBtn.addActionListener(e -> dispose());

        botoes.add(cancelarBtn);
        botoes.add(salvarBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(abas,   BorderLayout.CENTER);
        root.add(botoes, BorderLayout.SOUTH);
        add(root);
    }

    private JPanel buildDadosTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 20, 8, 20));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 4, 5, 4);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        nomeField     = new JTextField(20);
        raField       = new JTextField(20);
        cpfField      = new JTextField(20);
        emailField    = new JTextField(20);
        telefoneField = new JTextField(20);

        if (studentEdicao != null) {
            nomeField.setText(studentEdicao.getName());
            raField.setText(studentEdicao.getRa());
            cpfField.setText(studentEdicao.getCpf());
            emailField.setText(studentEdicao.getEmail() != null ? studentEdicao.getEmail() : "");
            emailField.setEditable(false);
            emailField.setBackground(new Color(0xF5F5F5));
            telefoneField.setText(studentEdicao.getPhone() != null ? studentEdicao.getPhone() : "");
        }

        addLinha(panel, gc, "Nome *",    nomeField,     0);
        addLinha(panel, gc, "RA *",      raField,       1);
        addLinha(panel, gc, "CPF *",     cpfField,      2);
        addLinha(panel, gc, "E-mail *",  emailField,    3);
        addLinha(panel, gc, "Telefone",  telefoneField, 4);

        if (studentEdicao == null) {
            JLabel hint = new JLabel("* Senha inicial: CPF sem formatação");
            hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            hint.setForeground(Color.GRAY);
            gc.gridx = 1; gc.gridy = 5;
            panel.add(hint, gc);
        }
        return panel;
    }

    private JPanel buildEnderecoTab() {
        Address a = studentEdicao != null ? studentEdicao.getAddress() : null;

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        if (a == null) {
            JLabel sem = new JLabel("Sem endereço cadastrado.");
            sem.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            sem.setForeground(Color.GRAY);
            panel.add(sem);
            return panel;
        }

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 0, 6, 12);
        gc.anchor = GridBagConstraints.WEST;

        String logradouro = a.getStreet() + ", " + a.getNumber()
            + (a.getComplement() != null && !a.getComplement().isBlank() ? " — " + a.getComplement() : "");

        String[][] linhas = {
            {"Logradouro:", logradouro},
            {"Bairro:",     a.getDistrict()},
            {"Cidade/UF:",  a.getCity() + " / " + a.getState()},
            {"CEP:",        a.formatarCep()},
        };

        for (int i = 0; i < linhas.length; i++) {
            gc.gridx = 0; gc.gridy = i; gc.weightx = 0;
            JLabel lbl = new JLabel(linhas[i][0]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setForeground(new Color(0x555555));
            lbl.setPreferredSize(new Dimension(90, 24));
            panel.add(lbl, gc);

            gc.gridx = 1; gc.weightx = 1;
            JLabel val = new JLabel(linhas[i][1] != null ? linhas[i][1] : "—");
            val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            panel.add(val, gc);
        }
        return panel;
    }

    private void addLinha(JPanel panel, GridBagConstraints gc, String label, JComponent field, int row) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setPreferredSize(new Dimension(90, 24));
        panel.add(lbl, gc);

        gc.gridx = 1; gc.gridy = row; gc.weightx = 1.0;
        if (field instanceof JTextField tf) tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(field, gc);
    }

    private void salvar() {
        String nome     = nomeField.getText().trim();
        String ra       = raField.getText().trim();
        String cpf      = cpfField.getText().replaceAll("[^\\d]", "");
        String email    = emailField.getText().trim();
        String telefone = telefoneField.getText().trim();

        Student student = studentEdicao != null ? studentEdicao : new Student();
        student.setName(nome);
        student.setRa(ra);
        student.setCpf(cpf);
        student.setEmail(email);
        student.setPhone(telefone.isBlank() ? null : telefone);

        try {
            if (studentEdicao == null) {
                service.criar(student);
            } else {
                service.editar(student);
            }
            saved = true;
            dispose();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
}
