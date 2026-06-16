package com.portal.util;

/**
 * ValidationUtil: utilitário com regras de VALIDAÇÃO e FORMATAÇÃO de dados.
 *
 * Concentra a lógica de verificar se e-mail, RA, CPF e CNPJ são válidos, além de
 * formatar CNPJ e telefone para exibição. Centralizar isso evita repetir as mesmas
 * regras em vários lugares e garante que todo o sistema valide do mesmo jeito.
 *
 * Vários métodos usam "expressões regulares" (regex): padrões de texto que descrevem
 * o formato esperado de uma string.
 */
public class ValidationUtil {

    /**
     * Valida um e-mail.
     * @return true se o texto tiver a cara de um e-mail (algo@dominio.xx).
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false; // Vazio ou nulo já é inválido.
        // Regex: letras/números/pontos antes do @, domínio, ponto e extensão de 2+ letras.
        return email.matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Valida um RA (Registro Acadêmico).
     * @return true se for composto apenas por 5 a 20 dígitos.
     */
    public static boolean isValidRa(String ra) {
        if (ra == null || ra.isBlank()) return false;
        return ra.matches("\\d{5,20}"); // \d = dígito; {5,20} = de 5 a 20 deles.
    }

    /**
     * Valida um CPF usando o algoritmo oficial dos dígitos verificadores.
     *
     * COMO FUNCIONA: o CPF tem 11 dígitos; os 2 últimos são "dígitos verificadores"
     * calculados a partir dos 9 primeiros. Recalculamos esses dígitos e comparamos
     * com os informados — se baterem, o CPF é matematicamente válido.
     *
     * @return true se o CPF for válido.
     */
    public static boolean isValidCpf(String cpf) {
        if (cpf == null) return false;
        String digits = cpf.replaceAll("[^\\d]", ""); // Mantém apenas os dígitos.

        if (digits.length() != 11) return false;               // Precisa ter 11 dígitos.
        if (digits.chars().distinct().count() == 1) return false; // Rejeita "111.111.111-11" etc.

        // ----- Cálculo do PRIMEIRO dígito verificador (posição 9) -----
        int sum = 0;
        // Multiplica os 9 primeiros dígitos por pesos decrescentes de 10 até 2.
        for (int i = 0; i < 9; i++) sum += (digits.charAt(i) - '0') * (10 - i);
        int first = (sum * 10 % 11) % 10;
        if (first != digits.charAt(9) - '0') return false; // Compara com o 10º dígito.

        // ----- Cálculo do SEGUNDO dígito verificador (posição 10) -----
        sum = 0;
        // Agora usa os 10 primeiros dígitos, com pesos de 11 até 2.
        for (int i = 0; i < 10; i++) sum += (digits.charAt(i) - '0') * (11 - i);
        int second = (sum * 10 % 11) % 10;
        return second == digits.charAt(10) - '0'; // Compara com o 11º dígito.
        // OBS: "digits.charAt(i) - '0'" converte o caractere ('0'..'9') no número que ele representa.
    }

    /**
     * Valida um CNPJ usando o algoritmo oficial dos dígitos verificadores.
     *
     * A lógica é parecida com a do CPF, mas o CNPJ tem 14 dígitos e usa pesos
     * específicos (arrays weights1 e weights2) para calcular os 2 dígitos finais.
     *
     * @return true se o CNPJ for válido.
     */
    public static boolean isValidCnpj(String cnpj) {
        if (cnpj == null) return false;
        String digits = cnpj.replaceAll("[^\\d]", ""); // Mantém apenas os dígitos.

        if (digits.length() != 14) return false;                  // Precisa ter 14 dígitos.
        if (digits.chars().distinct().count() == 1) return false; // Rejeita dígitos todos iguais.

        // Pesos oficiais usados no cálculo de cada dígito verificador.
        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        // ----- PRIMEIRO dígito verificador (posição 12) -----
        int sum = 0;
        for (int i = 0; i < 12; i++) sum += (digits.charAt(i) - '0') * weights1[i];
        int first = sum % 11 < 2 ? 0 : 11 - sum % 11; // Regra oficial: resto < 2 vira 0.
        if (first != digits.charAt(12) - '0') return false;

        // ----- SEGUNDO dígito verificador (posição 13) -----
        sum = 0;
        for (int i = 0; i < 13; i++) sum += (digits.charAt(i) - '0') * weights2[i];
        int second = sum % 11 < 2 ? 0 : 11 - sum % 11;
        return second == digits.charAt(13) - '0';
    }

    /**
     * Formata um CNPJ de 14 dígitos no padrão visual XX.XXX.XXX/XXXX-XX.
     * Se a entrada não tiver 14 dígitos, devolve o valor original sem mexer.
     */
    public static String formatCnpj(String cnpj) {
        if (cnpj == null) return "";
        String d = cnpj.replaceAll("[^\\d]", "");
        if (d.length() != 14) return cnpj;
        // Monta a máscara pegando pedaços (substrings) do número e intercalando os símbolos.
        return d.substring(0, 2) + "." + d.substring(2, 5) + "." +
               d.substring(5, 8) + "/" + d.substring(8, 12) + "-" + d.substring(12);
    }

    /**
     * Formata um telefone para exibição, escolhendo a máscara conforme a quantidade de dígitos:
     *  - 10 dígitos (fixo):    (XX) XXXX-XXXX
     *  - 11 dígitos (celular): (XX) XXXXX-XXXX
     * Se não for nem 10 nem 11 dígitos, devolve o valor original.
     */
    public static String formatPhone(String phone) {
        if (phone == null) return "";
        String d = phone.replaceAll("[^\\d]", "");
        if (d.length() == 11) { // Celular: 2 do DDD + 5 + 4.
            return "(" + d.substring(0, 2) + ") " + d.substring(2, 7) + "-" + d.substring(7);
        } else if (d.length() == 10) { // Fixo: 2 do DDD + 4 + 4.
            return "(" + d.substring(0, 2) + ") " + d.substring(2, 6) + "-" + d.substring(6);
        }
        return phone;
    }
}
