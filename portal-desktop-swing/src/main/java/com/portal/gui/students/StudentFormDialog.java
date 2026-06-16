package com.portal.gui.students;

import com.portal.model.Address;
import com.portal.model.Student;
import com.portal.service.ServiceException;
import com.portal.service.StudentService;
import com.portal.util.ButtonFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * StudentFormDialog: formulário (diálogo) para CRIAR ou EDITAR um aluno.
 *
 * Como nos outros formulários, a mesma tela serve para os dois casos: se "studentEdicao"
 * for null, é cadastro; se vier preenchido, é edição. Os dados ficam organizados em duas
 * abas: "Dados" (editável) e "Endereço" (somente leitura).
 */
public class StudentFormDialog extends JDialog {

    private final Student studentEdicao;     // Aluno em edição, ou null se for um novo.
    private final StudentService service;    // Serviço com as regras de negócio dos alunos.
    private boolean saved = false;           // Indica se houve salvamento (para recarregar a lista).

    // Campos do formulário.
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

    /** Monta o diálogo: cabeçalho, abas e botões. */
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

        // ── Abas: "Dados" (editável) e "Endereço" (leitura) ───────────────────
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        abas.addTab("Dados",    buildDadosTab());
        abas.addTab("Endereço", buildEnderecoTab());

        // ── Botões "Salvar" e "Cancelar" ──────────────────────────────────────
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

    /** Monta a aba "Dados" com os campos editáveis do aluno. */
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

        // Se estamos editando, preenche os campos com os dados atuais do aluno.
        if (studentEdicao != null) {
            nomeField.setText(studentEdicao.getName());
            raField.setText(studentEdicao.getRa());
            cpfField.setText(studentEdicao.getCpf());
            emailField.setText(studentEdicao.getEmail() != null ? studentEdicao.getEmail() : "");
            // Na edição, o e-mail NÃO pode ser alterado (é a base do login), então é travado.
            emailField.setEditable(false);
            emailField.setBackground(new Color(0xF5F5F5)); // Cinza para indicar campo desativado.
            telefoneField.setText(studentEdicao.getPhone() != null ? studentEdicao.getPhone() : "");
        }

        // Adiciona cada linha (rótulo + campo) na ordem. O * indica campo obrigatório.
        addLinha(panel, gc, "Nome *",    nomeField,     0);
        addLinha(panel, gc, "RA *",      raField,       1);
        addLinha(panel, gc, "CPF *",     cpfField,      2);
        addLinha(panel, gc, "E-mail *",  emailField,    3);
        addLinha(panel, gc, "Telefone",  telefoneField, 4);

        // Só na criação: lembra que a senha inicial do aluno será o CPF.
        if (studentEdicao == null) {
            JLabel hint = new JLabel("* Senha inicial: CPF sem formatação");
            hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            hint.setForeground(Color.GRAY);
            gc.gridx = 1; gc.gridy = 5;
            panel.add(hint, gc);
        }
        return panel;
    }

    /**
     * Monta a aba "Endereço" (apenas exibição). Se o aluno não tiver endereço,
     * mostra uma mensagem indicando isso.
     */
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

    /**
     * Método auxiliar que adiciona uma linha "rótulo + campo" ao formulário,
     * evitando repetir o mesmo código para cada campo.
     */
    private void addLinha(JPanel panel, GridBagConstraints gc, String label, JComponent field, int row) {
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setPreferredSize(new Dimension(90, 24));
        panel.add(lbl, gc);

        gc.gridx = 1; gc.gridy = row; gc.weightx = 1.0;
        // "instanceof tf" (pattern matching): se for um JTextField, já o usa com a variável tf.
        if (field instanceof JTextField tf) tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(field, gc);
    }

    /**
     * Lê os campos, monta o objeto Student e pede ao serviço para criar ou editar.
     * Erros de regra (ServiceException) viram um aviso amigável ao usuário.
     */
    private void salvar() {
        String nome     = nomeField.getText().trim();
        String ra       = raField.getText().trim();
        String cpf      = cpfField.getText().replaceAll("[^\\d]", ""); // Só dígitos.
        String email    = emailField.getText().trim();
        String telefone = telefoneField.getText().trim();

        // Na edição, reaproveita o objeto existente; na criação, cria um novo.
        Student student = studentEdicao != null ? studentEdicao : new Student();
        student.setName(nome);
        student.setRa(ra);
        student.setCpf(cpf);
        student.setEmail(email);
        student.setPhone(telefone.isBlank() ? null : telefone); // Telefone vazio vira null.

        try {
            if (studentEdicao == null) {
                service.criar(student);  // Cadastro novo.
            } else {
                service.editar(student); // Edição.
            }
            saved = true;
            dispose();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Informa à tela que abriu o diálogo se o aluno foi salvo. */
    public boolean isSaved() { return saved; }
}
