package com.portal.gui.course;

import com.portal.model.Course;
import com.portal.service.CourseService;
import com.portal.service.ServiceException;
import com.portal.util.ButtonFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * CourseFormDialog: diálogo (formulário) para CRIAR ou EDITAR um curso.
 *
 * A MESMA tela serve para os dois casos. A diferença é o objeto "courseToEdit":
 *   - se vier null, estamos CRIANDO um curso novo;
 *   - se vier preenchido, estamos EDITANDO aquele curso (os campos já vêm preenchidos).
 *
 * O campo "saved" informa a tela que abriu este diálogo se algo foi realmente salvo,
 * para que ela saiba se precisa recarregar a lista.
 */
public class CourseFormDialog extends JDialog {

    private final CourseService service = new CourseService(); // Regras de negócio dos cursos.
    private final Course courseToEdit; // Curso sendo editado, ou null se for criação.

    private JTextField nameField;       // Campo do nome do curso.
    private JTextField codeField;       // Campo do código (opcional).
    private JSpinner periodsSpinner;    // Seletor numérico para a quantidade de semestres.

    private boolean saved = false; // Vira true quando o curso é salvo com sucesso.

    /**
     * @param parent       janela pai.
     * @param courseToEdit curso a editar, ou null para criar um novo.
     */
    public CourseFormDialog(Frame parent, Course courseToEdit) {
        // Título da janela muda conforme o modo (novo x editar).
        super(parent, courseToEdit == null ? "Novo Curso" : "Editar Curso", true);
        this.courseToEdit = courseToEdit;
        setSize(400, 280);
        setLocationRelativeTo(parent);
        setResizable(false);
        build();
        // Se for edição, preenche os campos com os dados atuais do curso.
        if (courseToEdit != null) preencherCampos();
    }

    /** Monta o formulário (cabeçalho, campos e botões). */
    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // Cabeçalho azul com o título.
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(0x1565C0));
        header.setPreferredSize(new Dimension(400, 56));
        JLabel titulo = new JLabel(courseToEdit == null ? "Novo Curso" : "Editar Curso");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        header.add(titulo);

        // Área dos campos do formulário.
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 28, 12, 28));

        nameField    = new JTextField();
        codeField    = new JTextField();
        // SpinnerNumberModel(valorInicial=4, mínimo=1, máximo=20, passo=1):
        // limita os semestres ao intervalo 1..20 e começa em 4.
        periodsSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; // Campos ocupam a largura disponível.
        gc.insets = new Insets(4, 0, 4, 0);

        // Linha 0: rótulo "Nome *" + campo do nome (o * indica obrigatório).
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.35;
        form.add(label("Nome *"), gc);
        gc.gridx = 1; gc.weightx = 0.65;
        form.add(nameField, gc);

        // Linha 1: rótulo "Código" + campo do código.
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0.35;
        form.add(label("Código"), gc);
        gc.gridx = 1; gc.weightx = 0.65;
        form.add(codeField, gc);

        // Linha 2: rótulo "Semestres *" + spinner de semestres.
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0.35;
        form.add(label("Semestres *"), gc);
        gc.gridx = 1; gc.weightx = 0.65;
        form.add(periodsSpinner, gc);

        // Botões "Cancelar" e "Salvar" no rodapé.
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botoes.setBackground(Color.WHITE);

        JButton cancelar = ButtonFactory.secondary("Cancelar");
        JButton salvar   = ButtonFactory.primary("Salvar");

        cancelar.addActionListener(e -> dispose()); // Fecha sem salvar.
        salvar.addActionListener(e -> salvar());    // Tenta salvar.
        getRootPane().setDefaultButton(salvar);     // ENTER aciona "Salvar".

        botoes.add(cancelar);
        botoes.add(salvar);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(botoes, BorderLayout.SOUTH);
        add(root);
    }

    /** Preenche os campos com os dados do curso em edição. */
    private void preencherCampos() {
        nameField.setText(courseToEdit.getName());
        // Código pode ser nulo; nesse caso, mostra string vazia.
        codeField.setText(courseToEdit.getCode() != null ? courseToEdit.getCode() : "");
        periodsSpinner.setValue(courseToEdit.getPeriods());
    }

    /**
     * Tenta salvar o curso (criar ou editar, conforme o modo).
     * Trata os erros mostrando mensagens apropriadas ao usuário.
     */
    private void salvar() {
        String name    = nameField.getText();
        String code    = codeField.getText();
        int periods    = (int) periodsSpinner.getValue();
        try {
            if (courseToEdit == null) {
                service.criar(name, code, periods);   // Modo criação.
            } else {
                service.editar(courseToEdit, name, code, periods); // Modo edição.
            }
            saved = true; // Marca que houve salvamento.
            dispose();    // Fecha o diálogo.
        } catch (ServiceException ex) {
            // Erro de REGRA (ex.: nome duplicado): aviso amigável ao usuário.
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            // Erro inesperado (técnico): mensagem de erro + detalhes no console.
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /** Permite à tela que abriu o diálogo saber se o curso foi salvo (para recarregar a lista). */
    public boolean isSaved() { return saved; }

    /** Cria um rótulo padronizado para os campos do formulário. */
    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }
}
