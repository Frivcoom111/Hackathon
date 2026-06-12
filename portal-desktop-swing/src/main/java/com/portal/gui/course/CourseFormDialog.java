package com.portal.gui.course;

import com.portal.model.Course;
import com.portal.service.CourseService;
import com.portal.service.ServiceException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CourseFormDialog extends JDialog {

    private final CourseService service = new CourseService();
    private final Course courseToEdit;

    private JTextField nameField;
    private JTextField codeField;
    private JSpinner periodsSpinner;

    private boolean saved = false;

    public CourseFormDialog(Frame parent, Course courseToEdit) {
        super(parent, courseToEdit == null ? "Novo Curso" : "Editar Curso", true);
        this.courseToEdit = courseToEdit;
        setSize(400, 280);
        setLocationRelativeTo(parent);
        setResizable(false);
        build();
        if (courseToEdit != null) preencherCampos();
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(0x1565C0));
        header.setPreferredSize(new Dimension(400, 56));
        JLabel titulo = new JLabel(courseToEdit == null ? "Novo Curso" : "Editar Curso");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        header.add(titulo);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 28, 12, 28));

        nameField    = new JTextField();
        codeField    = new JTextField();
        periodsSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(4, 0, 4, 0);

        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.35;
        form.add(label("Nome *"), gc);
        gc.gridx = 1; gc.weightx = 0.65;
        form.add(nameField, gc);

        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0.35;
        form.add(label("Código"), gc);
        gc.gridx = 1; gc.weightx = 0.65;
        form.add(codeField, gc);

        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0.35;
        form.add(label("Semestres *"), gc);
        gc.gridx = 1; gc.weightx = 0.65;
        form.add(periodsSpinner, gc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        botoes.setBackground(Color.WHITE);

        JButton cancelar = new JButton("Cancelar");
        JButton salvar   = new JButton("Salvar");

        estilizarBotaoSecundario(cancelar);
        estilizarBotaoPrimario(salvar);

        cancelar.addActionListener(e -> dispose());
        salvar.addActionListener(e -> salvar());
        getRootPane().setDefaultButton(salvar);

        botoes.add(cancelar);
        botoes.add(salvar);

        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(botoes, BorderLayout.SOUTH);
        add(root);
    }

    private void preencherCampos() {
        nameField.setText(courseToEdit.getName());
        codeField.setText(courseToEdit.getCode() != null ? courseToEdit.getCode() : "");
        periodsSpinner.setValue(courseToEdit.getPeriods());
    }

    private void salvar() {
        String name    = nameField.getText();
        String code    = codeField.getText();
        int periods    = (int) periodsSpinner.getValue();
        try {
            if (courseToEdit == null) {
                service.criar(name, code, periods);
            } else {
                service.editar(courseToEdit, name, code, periods);
            }
            saved = true;
            dispose();
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public boolean isSaved() { return saved; }

    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private void estilizarBotaoPrimario(JButton btn) {
        btn.setBackground(new Color(0x1565C0));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void estilizarBotaoSecundario(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
