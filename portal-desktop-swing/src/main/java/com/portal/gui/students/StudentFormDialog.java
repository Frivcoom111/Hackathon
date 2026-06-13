package com.portal.gui.students;

import com.portal.model.Student;
import com.portal.service.ServiceException;
import com.portal.service.StudentService;

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
        setSize(460, 380);
        setLocationRelativeTo(parent);
        setResizable(false);
        build();
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(20, 24, 16, 24));

        // ── Título ────────────────────────────────────────────────────────────
        JLabel titulo = new JLabel(studentEdicao == null ? "Cadastrar Aluno" : "Editar Aluno");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(new Color(0x1A237E));
        titulo.setBorder(new EmptyBorder(0, 0, 16, 0));

        // ── Formulário ────────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
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

        addLinha(form, gc, "Nome *",    nomeField,     0);
        addLinha(form, gc, "RA *",      raField,       1);
        addLinha(form, gc, "CPF *",     cpfField,      2);
        addLinha(form, gc, "E-mail *",  emailField,    3);
        addLinha(form, gc, "Telefone",  telefoneField, 4);

        if (studentEdicao == null) {
            JLabel hint = new JLabel("* Senha inicial: CPF sem formatação");
            hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            hint.setForeground(Color.GRAY);
            gc.gridx = 1; gc.gridy = 5;
            form.add(hint, gc);
        }

        // ── Botões ────────────────────────────────────────────────────────────
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setBackground(Color.WHITE);
        botoes.setBorder(new EmptyBorder(16, 0, 0, 0));

        JButton salvarBtn  = criarBotao("Salvar", new Color(0x1565C0));
        JButton cancelarBtn = criarBotao("Cancelar", new Color(0x616161));

        salvarBtn.addActionListener(e -> salvar());
        cancelarBtn.addActionListener(e -> dispose());

        botoes.add(cancelarBtn);
        botoes.add(salvarBtn);

        root.add(titulo, BorderLayout.NORTH);
        root.add(form,   BorderLayout.CENTER);
        root.add(botoes, BorderLayout.SOUTH);
        add(root);
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

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public boolean isSaved() { return saved; }
}
