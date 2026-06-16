// Pacote "gui.login": tela de login da aplicação (parte da interface gráfica).
package com.portal.gui.login;

// Próxima tela que abre após o login bem-sucedido, por isso a importação.
import com.portal.gui.dashboard.DashboardFrame;

import com.portal.service.AuthException; // Exceção lançada quando o login falha.

// Classe responsável pela autenticação (validação de e-mail, senha, perfil, etc.).
import com.portal.service.AuthService;

import javax.swing.*;                 // Componentes visuais do Swing (JFrame, JButton...).
import javax.swing.border.EmptyBorder; // Cria espaçamentos (margens internas) nos painéis.
import java.awt.*;                    // Classes gráficas básicas (Color, Font, layouts...).

/**
 * LoginFrame: a janela (tela) de LOGIN do sistema.
 *
 * "extends JFrame" significa que esta classe É uma janela do Swing. No construtor,
 * montamos visualmente a tela: um cabeçalho azul no topo e um formulário com os campos
 * de e-mail e senha. Ao clicar em "Entrar", o login é validado pelo AuthService.
 */
public class LoginFrame extends JFrame {
    private JTextField emailField;       // Campo de texto onde o usuário digita o e-mail.
    private JPasswordField senhaField;   // Campo de senha (esconde os caracteres digitados).
    private final AuthService authService = new AuthService(); // Serviço que valida o login.


    /** Construtor: configura a janela e monta todos os componentes visuais. */
    public LoginFrame() {
        setTitle("Portal UniALFA");          // Texto na barra de título da janela.
        setSize(420, 480);                   // Tamanho da janela (largura x altura, em pixels).
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Ao fechar a janela, encerra o programa.
        setLocationRelativeTo(null);         // Centraliza a janela na tela.
        setResizable(false);                 // Impede que o usuário redimensione a janela.

        // Painel raiz, que organiza o conteúdo usando BorderLayout (Norte, Sul, Centro...).
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // --- Cabeçalho azul (header) ---
        // GridBagLayout permite centralizar o título e o subtítulo verticalmente.
        JPanel header = new JPanel(new GridBagLayout());
        header.setBackground(new Color(0x1565C0)); // Azul padrão do sistema.
        header.setPreferredSize(new Dimension(420, 140));

        JLabel titulo = new JLabel("Portal UniALFA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Sistema de Gestão de Estágios");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(0xBBDEFB)); // Azul bem claro.

        // GridBagConstraints define a posição de cada componente dentro do GridBagLayout.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(2, 0, 2, 0);
        header.add(titulo, gbc);
        gbc.gridy = 1; // Próxima linha (abaixo do título).
        header.add(subtitulo, gbc);

        // --- Formulário (campos de e-mail e senha) ---
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        // BoxLayout no eixo Y empilha os componentes verticalmente, um abaixo do outro.
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(new EmptyBorder(32, 48, 32, 48)); // Margem interna (cima, esq, baixo, dir).

        emailField = new JTextField();
        senhaField = new JPasswordField();
        JButton entrarBtn = new JButton("Entrar");

        // Aplica o estilo visual padronizado aos campos e ao botão (métodos auxiliares abaixo).
        estilizarCampo(emailField);
        estilizarCampo(senhaField);
        estilizarBotao(entrarBtn);

        // Monta o formulário, intercalando rótulos, campos e espaços verticais (struts).
        form.add(label("E-mail"));
        form.add(Box.createVerticalStrut(6));  // Espaço vazio de 6px.
        form.add(emailField);
        form.add(Box.createVerticalStrut(18));
        form.add(label("Senha"));
        form.add(Box.createVerticalStrut(6));
        form.add(senhaField);
        form.add(Box.createVerticalStrut(28));
        form.add(entrarBtn);

        // Posiciona o cabeçalho no topo (NORTH) e o formulário no centro (CENTER).
        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        add(root); // Adiciona o painel raiz à janela.

        // Liga o clique do botão "Entrar" ao método realizarLogin (programação por eventos).
        entrarBtn.addActionListener(e -> realizarLogin());
        // Define "Entrar" como botão padrão: pressionar ENTER também faz o login.
        getRootPane().setDefaultButton(entrarBtn);
    }

    /**
     * Executa a tentativa de login. Chamado quando o usuário clica em "Entrar" (ou tecla ENTER).
     */
    private void realizarLogin() {
        String email = emailField.getText().trim();
        // getPassword() devolve um array de char; convertemos para String para validar.
        String senha = new String(senhaField.getPassword());
        try {
            authService.login(email, senha); // Valida as credenciais (pode lançar AuthException).
            dispose();                        // Fecha a tela de login.
            new DashboardFrame().setVisible(true); // Abre o painel principal.
        } catch (AuthException ex) {
            // Se o login falhar, mostra uma caixa de diálogo com a mensagem de erro.
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Cria um rótulo (texto) padronizado para os campos do formulário. */
    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(new Color(0x616161)); // Cinza.
        l.setAlignmentX(Component.LEFT_ALIGNMENT); // Alinha à esquerda dentro do BoxLayout.
        return l;
    }

    /** Aplica o estilo visual padrão a um campo de texto (fonte, altura e borda). */
    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Largura total, altura fixa.
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Borda composta: uma linha cinza por fora + um espaçamento interno (padding).
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xBDBDBD)),
            new EmptyBorder(4, 10, 4, 10)
        ));
    }

    /** Aplica o estilo visual padrão ao botão "Entrar" (azul, branco, em negrito). */
    private void estilizarBotao(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(new Color(0x1565C0));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);   // Remove a borda de foco pontilhada.
        btn.setBorderPainted(false);
        btn.setOpaque(true);          // Garante que o fundo seja pintado.
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Cursor de mãozinha.
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    }
}
