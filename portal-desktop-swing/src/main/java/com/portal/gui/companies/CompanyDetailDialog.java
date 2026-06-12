package com.portal.gui.companies;

import com.portal.model.Company;
import com.portal.model.enums.CompanyStatus;
import com.portal.service.CompanyService;
import com.portal.service.ServiceException;
import com.portal.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CompanyDetailDialog extends JDialog {

    private final Company company;
    private final CompanyService service;

    private JLabel statusLabel;
    private JButton analisarBtn;
    private JButton aprovarBtn;
    private JButton bloquearBtn;

    public CompanyDetailDialog(Frame parent, Company company, CompanyService service) {
        super(parent, "Detalhes da Empresa", true);
        this.company = company;
        this.service = service;
        setSize(520, 440);
        setLocationRelativeTo(parent);
        setResizable(false);
        build();
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(20, 24, 16, 24));

        // ── Cabeçalho ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel nome = new JLabel(company.getName());
        nome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nome.setForeground(new Color(0x1A237E));

        statusLabel = new JLabel(company.getStatus().name());
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        statusLabel.setOpaque(true);
        atualizarCorStatus();

        header.add(nome, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);

        // ── Dados cadastrais ───────────────────────────────────────────────────
        JPanel dados = new JPanel(new GridBagLayout());
        dados.setBackground(Color.WHITE);
        dados.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0)), "Dados Cadastrais",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12), new Color(0x1565C0)));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 8, 4, 8);
        gc.anchor = GridBagConstraints.WEST;

        addCampo(dados, gc, "CNPJ:",     ValidationUtil.formatCnpj(company.getCnpj()), 0);
        addCampo(dados, gc, "Telefone:", company.getPhone() != null ? ValidationUtil.formatPhone(company.getPhone()) : "Não informado", 1);

        // Descrição (área de texto somente leitura)
        gc.gridx = 0; gc.gridy = 2; gc.gridwidth = 1;
        JLabel lblDesc = new JLabel("Descrição:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dados.add(lblDesc, gc);

        gc.gridx = 1; gc.gridy = 2; gc.gridwidth = 2; gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1.0; gc.weighty = 1.0;
        String descTexto = company.getDescription() != null ? company.getDescription() : "Sem descrição";
        JTextArea descArea = new JTextArea(descTexto);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setEditable(false);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setBackground(new Color(0xFAFAFA));
        descArea.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(300, 80));
        descScroll.setBorder(null);
        dados.add(descScroll, gc);

        gc.weightx = 0; gc.weighty = 0; gc.fill = GridBagConstraints.NONE;

        // ── Ações ──────────────────────────────────────────────────────────────
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acoes.setBackground(Color.WHITE);
        acoes.setBorder(new EmptyBorder(16, 0, 0, 0));

        analisarBtn = criarBotao("Iniciar Análise", new Color(0x1565C0));
        aprovarBtn  = criarBotao("Aprovar",         new Color(0x2E7D32));
        bloquearBtn = criarBotao("Bloquear",        new Color(0xC62828));
        JButton fecharBtn = criarBotao("Fechar",    new Color(0x616161));

        analisarBtn.addActionListener(e -> executarAcao("analisar"));
        aprovarBtn.addActionListener(e -> executarAcao("aprovar"));
        bloquearBtn.addActionListener(e -> executarAcao("bloquear"));
        fecharBtn.addActionListener(e -> dispose());

        atualizarBotoes();

        acoes.add(analisarBtn);
        acoes.add(aprovarBtn);
        acoes.add(bloquearBtn);
        acoes.add(fecharBtn);

        root.add(header, BorderLayout.NORTH);
        root.add(dados,  BorderLayout.CENTER);
        root.add(acoes,  BorderLayout.SOUTH);
        add(root);
    }

    private void addCampo(JPanel panel, GridBagConstraints gc, String label, String valor, int row) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, gc);

        gc.gridx = 1; gc.gridy = row; gc.gridwidth = 2;
        JLabel val = new JLabel(valor);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(val, gc);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void executarAcao(String acao) {
        String label = switch (acao) {
            case "analisar" -> "iniciar análise de";
            case "aprovar"  -> "aprovar";
            case "bloquear" -> "bloquear";
            default -> acao;
        };
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja " + label + " a empresa \"" + company.getName() + "\"?",
            "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            switch (acao) {
                case "analisar" -> service.analisar(company);
                case "aprovar"  -> service.aprovar(company);
                case "bloquear" -> service.bloquear(company);
            }
            atualizarCorStatus();
            atualizarBotoes();
            JOptionPane.showMessageDialog(this, "Status atualizado com sucesso.", "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (ServiceException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarCorStatus() {
        statusLabel.setText(company.getStatus().name());
        switch (company.getStatus()) {
            case PENDING   -> { statusLabel.setBackground(new Color(0xFFF9C4)); statusLabel.setForeground(new Color(0xF57F17)); }
            case ANALYSING -> { statusLabel.setBackground(new Color(0xE3F2FD)); statusLabel.setForeground(new Color(0x1565C0)); }
            case APPROVED  -> { statusLabel.setBackground(new Color(0xE8F5E9)); statusLabel.setForeground(new Color(0x2E7D32)); }
            case BLOCKED   -> { statusLabel.setBackground(new Color(0xFFEBEE)); statusLabel.setForeground(new Color(0xC62828)); }
        }
    }

    private void atualizarBotoes() {
        CompanyStatus s = company.getStatus();
        analisarBtn.setEnabled(s == CompanyStatus.PENDING);
        aprovarBtn.setEnabled(s == CompanyStatus.ANALYSING || s == CompanyStatus.BLOCKED);
        bloquearBtn.setEnabled(s != CompanyStatus.BLOCKED);
    }
}
