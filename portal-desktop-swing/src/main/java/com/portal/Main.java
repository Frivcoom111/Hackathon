// Pacote raiz da aplicação. Todo o código fica organizado dentro de "com.portal".
package com.portal;

// Importa a janela de login (primeira tela que o usuário vê ao abrir o sistema).
import com.portal.gui.login.LoginFrame;
// Utilitário do Swing usado para executar código com segurança na thread de interface gráfica.
import javax.swing.SwingUtilities;

/**
 * Classe Main: é o PONTO DE ENTRADA da aplicação.
 *
 * Em Java, todo programa começa pelo método main(). Quando você roda o projeto,
 * a JVM (máquina virtual Java) procura esse método e executa o que está dentro dele.
 *
 * Aqui o objetivo é simplesmente abrir a tela de login.
 */
public class Main {

    /**
     * Método main: primeiro código executado quando o programa inicia.
     *
     * @param args argumentos de linha de comando (não são usados neste projeto).
     */
    public static void main(String[] args) {
        // SwingUtilities.invokeLater() garante que a criação da interface gráfica
        // aconteça na "Event Dispatch Thread" (EDT), que é a thread oficial do Swing
        // para mexer em telas. Isso evita travamentos e erros de concorrência.
        //
        // A expressão "() -> new LoginFrame().setVisible(true)" é uma função lambda:
        // ela cria a janela de login (LoginFrame) e a torna visível na tela.
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
