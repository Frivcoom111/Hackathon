// Pacote "util": guarda classes utilitárias com funções de apoio reutilizáveis.
package com.portal.util;

// BCrypt: biblioteca de criptografia de senhas. Ela transforma a senha em um "hash"
// (texto embaralhado e irreversível), de modo que a senha real nunca fique exposta.
import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * PasswordUtil: utilitário para lidar com senhas de forma segura.
 *
 * POR QUE USAR HASH? As senhas nunca devem ser guardadas em texto puro no banco.
 * Em vez disso, guardamos um "hash" gerado pelo BCrypt. Na hora do login, comparamos
 * a senha digitada com esse hash — sem nunca precisar "descriptografar" nada.
 */
public class PasswordUtil {

    /**
     * Verifica se uma senha digitada corresponde ao hash salvo no banco.
     *
     * @param rawPassword    a senha em texto puro, digitada pelo usuário no login.
     * @param hashedPassword o hash da senha que está armazenado no banco.
     * @return true se a senha confere com o hash; false caso contrário.
     */
    public static boolean verify(String rawPassword, String hashedPassword) {
        // O BCrypt recalcula e compara internamente; nunca revela a senha original.
        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), hashedPassword);
        // O campo "verified" é true quando a senha bate com o hash.
        return result.verified;
    }
}
