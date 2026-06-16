package com.portal.gui.companies;

import com.portal.dao.CompanyMemberDAO;
import com.portal.model.Company;
import com.portal.model.CompanyMember;
import com.portal.model.enums.CompanyStatus;
import com.portal.service.CompanyService;
import com.portal.service.ServiceException;
import com.portal.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.List;

/**
 * CompanyDetailDialog: diálogo de DETALHES e GERENCIAMENTO de uma empresa.
 *
 * É a tela mais rica do módulo de empresas. Mostra, em abas, os dados cadastrais e os
 * usuários vinculados, e oferece os botões de ação do fluxo de aprovação:
 *   - Iniciar Análise (PENDING → ANALYSING)
 *   - Aprovar         (libera a empresa)
 *   - Bloquear        (suspende a empresa)
 *
 * Os botões habilitam/desabilitam conforme o status atual, e o "badge" de status muda
 * de cor para refletir a situação.
 */
public class CompanyDetailDialog extends JDialog {

    private final Company company;             // A empresa exibida/gerenciada.
    private final CompanyService service;      // Regras de negócio (analisar/aprovar/bloquear).
    private final CompanyMemberDAO memberDAO = new CompanyMemberDAO(); // Busca os membros da empresa.

    private JLabel statusLabel;   // "Badge" colorido que mostra o status atual.
    private JButton analisarBtn;
    private JButton aprovarBtn;
    private JButton bloquearBtn;

    public CompanyDetailDialog(Frame parent, Company company, CompanyService service) {
        super(parent, "Detalhes da Empresa", true);
        this.company = company;
        this.service = service;
        setSize(620, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        build();
    }

    /** Monta a estrutura do diálogo: cabeçalho, abas e barra de ações. */
    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(16, 20, 12, 20));

        root.add(buildHeader(),  BorderLayout.NORTH);
        root.add(buildTabs(),    BorderLayout.CENTER);
        root.add(buildAcoes(),   BorderLayout.SOUTH);

        add(root);
    }

    // ── Cabeçalho: nome da empresa + badge de status ──────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel nome = new JLabel(company.getName());
        nome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nome.setForeground(new Color(0x1A237E));

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        statusLabel.setOpaque(true);
        atualizarCorStatus(); // Define o texto e a cor do badge conforme o status atual.

        header.add(nome,        BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);
        return header;
    }

    // ── Abas: "Dados da Empresa" e "Usuários Vinculados" ──────────────────────

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.addTab("Dados da Empresa",      buildTabDados());
        tabs.addTab("Usuários Vinculados",   buildTabUsuarios());
        return tabs;
    }

    // ── Aba 1: dados cadastrais (CNPJ, telefone, descrição) ───────────────────

    private JPanel buildTabDados() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 8, 8, 8));

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 8, 5, 8);
        gc.anchor = GridBagConstraints.WEST;

        // Campos simples (rótulo + valor), já formatando CNPJ e telefone.
        addCampo(panel, gc, "CNPJ:",     ValidationUtil.formatCnpj(company.getCnpj()), 0);
        addCampo(panel, gc, "Telefone:", company.getPhone() != null
                ? ValidationUtil.formatPhone(company.getPhone()) : "Não informado", 1);

        // Rótulo "Descrição:".
        gc.gridx = 0; gc.gridy = 2;
        JLabel lblDesc = new JLabel("Descrição:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lblDesc, gc);

        // A descrição usa um JTextArea (área de texto com várias linhas), somente leitura
        // e com rolagem, pois pode ser um texto longo.
        gc.gridx = 1; gc.gridy = 2; gc.gridwidth = 2;
        gc.fill = GridBagConstraints.BOTH; gc.weightx = 1.0; gc.weighty = 1.0;
        String descTexto = company.getDescription() != null ? company.getDescription() : "Sem descrição.";
        JTextArea descArea = new JTextArea(descTexto);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descArea.setEditable(false);     // Só leitura.
        descArea.setWrapStyleWord(true); // Quebra respeitando palavras inteiras.
        descArea.setLineWrap(true);      // Quebra a linha automaticamente quando chega na borda.
        descArea.setBackground(new Color(0xFAFAFA));
        descArea.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(340, 90));
        descScroll.setBorder(null);
        panel.add(descScroll, gc);

        return panel;
    }

    // ── Aba 2: usuários vinculados (dados de CompanyMember + User) ─────────────

    private JPanel buildTabUsuarios() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(12, 8, 8, 8));

        // Busca no banco os membros desta empresa.
        List<CompanyMember> membros = memberDAO.findByCompanyId(company.getId());

        MemberTableModel model = new MemberTableModel(membros);
        JTable tabela = new JTable(model);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(30);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(0xE3F2FD));
        tabela.setGridColor(new Color(0xE0E0E0));
        tabela.setShowVerticalLines(false);

        // Centraliza o "Perfil"; a "Situação" usa um renderer próprio que a colore.
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(2).setCellRenderer(center);
        tabela.getColumnModel().getColumn(3).setCellRenderer(new SituacaoRenderer());

        tabela.getColumnModel().getColumn(0).setPreferredWidth(180);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(90);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xE0E0E0)));
        scroll.getViewport().setBackground(Color.WHITE);

        // Se não houver membros, mostra uma mensagem no lugar da tabela.
        if (membros.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum usuário vinculado a esta empresa.", SwingConstants.CENTER);
            vazio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            vazio.setForeground(Color.GRAY);
            panel.add(vazio, BorderLayout.CENTER);
        } else {
            panel.add(scroll, BorderLayout.CENTER);
        }

        return panel;
    }

    // ── Barra de botões de ação (rodapé) ──────────────────────────────────────

    private JPanel buildAcoes() {
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acoes.setBackground(Color.WHITE);
        acoes.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Cada ação tem sua cor: azul (análise), verde (aprovar), vermelho (bloquear), cinza (fechar).
        analisarBtn = criarBotao("Iniciar Análise", new Color(0x1565C0));
        aprovarBtn  = criarBotao("Aprovar",         new Color(0x2E7D32));
        bloquearBtn = criarBotao("Bloquear",        new Color(0xC62828));
        JButton fecharBtn = criarBotao("Fechar",    new Color(0x616161));

        analisarBtn.addActionListener(e -> executarAcao("analisar"));
        aprovarBtn.addActionListener(e -> executarAcao("aprovar"));
        bloquearBtn.addActionListener(e -> executarAcao("bloquear"));
        fecharBtn.addActionListener(e -> dispose());

        atualizarBotoes(); // Liga/desliga os botões conforme o status atual.

        acoes.add(analisarBtn);
        acoes.add(aprovarBtn);
        acoes.add(bloquearBtn);
        acoes.add(fecharBtn);
        return acoes;
    }

    // ── Métodos auxiliares (helpers) ──────────────────────────────────────────

    /** Adiciona uma linha "rótulo + valor" simples ao painel de dados. */
    private void addCampo(JPanel panel, GridBagConstraints gc, String label, String valor, int row) {
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1; gc.fill = GridBagConstraints.NONE;
        gc.weightx = 0; gc.weighty = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, gc);

        gc.gridx = 1; gc.gridy = row; gc.gridwidth = 2;
        JLabel val = new JLabel(valor);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(val, gc);
    }

    /** Cria um botão de ação com cor de fundo e estilo padronizados. */
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

    /**
     * Executa uma das ações de mudança de status (analisar/aprovar/bloquear),
     * sempre pedindo confirmação antes e tratando os possíveis erros.
     *
     * @param acao identificador da ação: "analisar", "aprovar" ou "bloquear".
     */
    private void executarAcao(String acao) {
        // Monta o texto da pergunta de confirmação conforme a ação.
        String label = switch (acao) {
            case "analisar" -> "iniciar análise de";
            case "aprovar"  -> "aprovar";
            case "bloquear" -> "bloquear";
            default -> acao;
        };
        int confirm = JOptionPane.showConfirmDialog(this,
            "Deseja " + label + " a empresa \"" + company.getName() + "\"?",
            "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return; // Usuário desistiu.

        try {
            // Chama o método correspondente do serviço (que aplica as regras de negócio).
            switch (acao) {
                case "analisar" -> service.analisar(company);
                case "aprovar"  -> service.aprovar(company);
                case "bloquear" -> service.bloquear(company);
            }
            // Após mudar o status, atualiza o badge e os botões na tela.
            atualizarCorStatus();
            atualizarBotoes();
            JOptionPane.showMessageDialog(this, "Status atualizado com sucesso.", "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (ServiceException ex) {
            // Erro de regra (ex.: empresa já aprovada): aviso amigável.
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            // Erro inesperado.
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Atualiza o texto e as cores do badge de status conforme a situação da empresa. */
    private void atualizarCorStatus() {
        statusLabel.setText(company.getStatus().name());
        switch (company.getStatus()) {
            case PENDING   -> { statusLabel.setBackground(new Color(0xFFF9C4)); statusLabel.setForeground(new Color(0xF57F17)); }
            case ANALYSING -> { statusLabel.setBackground(new Color(0xE3F2FD)); statusLabel.setForeground(new Color(0x1565C0)); }
            case APPROVED  -> { statusLabel.setBackground(new Color(0xE8F5E9)); statusLabel.setForeground(new Color(0x2E7D32)); }
            case BLOCKED   -> { statusLabel.setBackground(new Color(0xFFEBEE)); statusLabel.setForeground(new Color(0xC62828)); }
        }
    }

    /**
     * Habilita/desabilita os botões conforme o status atual, garantindo que apenas as
     * transições válidas fiquem disponíveis:
     *   - "Iniciar Análise": só faz sentido se a empresa está PENDING.
     *   - "Aprovar": disponível se está em ANÁLISE ou BLOQUEADA.
     *   - "Bloquear": disponível em qualquer status, exceto se já estiver BLOQUEADA.
     */
    private void atualizarBotoes() {
        CompanyStatus s = company.getStatus();
        analisarBtn.setEnabled(s == CompanyStatus.PENDING);
        aprovarBtn.setEnabled(s == CompanyStatus.ANALYSING || s == CompanyStatus.BLOCKED);
        bloquearBtn.setEnabled(s != CompanyStatus.BLOCKED);
    }

    // ── TableModel dos membros vinculados ─────────────────────────────────────

    private static class MemberTableModel extends AbstractTableModel {
        private final String[] COLS = {"Nome", "E-mail", "Perfil", "Situação"};
        private final List<CompanyMember> membros;

        MemberTableModel(List<CompanyMember> membros) { this.membros = membros; }

        @Override public int getRowCount()    { return membros.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int col) { return COLS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            CompanyMember m = membros.get(row);
            return switch (col) {
                case 0 -> m.getName();
                case 1 -> m.getEmail();
                case 2 -> m.getRole().name();
                case 3 -> m.isUserActive() ? "Ativo" : "Inativo";
                default -> "";
            };
        }
    }

    // ── Renderer que pinta a coluna "Situação" (verde = Ativo, vermelho = Inativo) ──

    private static class SituacaoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            // Só colore quando a linha não está selecionada (para não atrapalhar o destaque).
            if (!isSelected) {
                if ("Ativo".equals(value)) {
                    setBackground(new Color(0xE8F5E9)); setForeground(new Color(0x2E7D32)); // Verde.
                } else {
                    setBackground(new Color(0xFFEBEE)); setForeground(new Color(0xC62828)); // Vermelho.
                }
            }
            return this;
        }
    }
}
