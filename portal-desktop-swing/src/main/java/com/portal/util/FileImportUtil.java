package com.portal.util;

import com.portal.model.Student; // Entidade que representa o aluno que será importado.

import java.io.BufferedReader; // Lê o arquivo de forma eficiente, linha por linha.
import java.io.FileReader;     // Abre o arquivo de texto para leitura.
import java.io.IOException;    // Exceção lançada quando há erro de leitura de arquivo.
import java.util.ArrayList;    // Lista dinâmica para acumular os alunos lidos.
import java.util.List;

/**
 * FileImportUtil: utilitário para IMPORTAR alunos a partir de um arquivo de texto.
 *
 * Permite cadastrar vários alunos de uma vez, lendo um arquivo onde cada linha
 * descreve um aluno no formato:  nome;ra;cpf;email
 *
 * Linhas em branco ou que começam com "#" (comentários) são ignoradas, assim como
 * linhas com dados inválidos — tornando a importação robusta a pequenos erros do arquivo.
 */
public class FileImportUtil {

    /**
     * Lê o arquivo informado e devolve a lista de alunos válidos encontrados.
     * Formato esperado de cada linha: nome;ra;cpf;email
     *
     * @param filePath caminho do arquivo a ser lido.
     * @return lista de alunos (Student) montada a partir das linhas válidas.
     * @throws IOException se o arquivo não puder ser lido.
     */
    public static List<Student> parseStudents(String filePath) throws IOException {
        List<Student> students = new ArrayList<>(); // Acumula os alunos válidos.

        // try-with-resources: o BufferedReader é fechado automaticamente ao final,
        // mesmo que ocorra um erro no meio da leitura (evita vazamento de recursos).
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0; // Contador de linhas, útil para mensagens de erro.

            // Lê o arquivo linha a linha até chegar ao fim (readLine retorna null no fim).
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim(); // Remove espaços em branco no início e no fim.

                // Pula linhas vazias ou comentários (que começam com "#").
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Divide a linha nos pontos-e-vírgula, separando os campos.
                String[] fields = line.split(";");
                // Se não houver pelo menos 4 campos, a linha está incompleta: ignora e avisa.
                if (fields.length < 4) {
                    System.err.println("Linha " + lineNumber + " ignorada (campos insuficientes): " + line);
                    continue;
                }

                // Extrai e limpa cada campo.
                String name  = fields[0].trim();
                String ra    = fields[1].trim();
                String cpf   = fields[2].replaceAll("[^\\d]", ""); // Remove tudo que não for dígito.
                String email = fields[3].trim();

                // Validações: se algum dado for inválido, a linha é descartada com aviso no console.
                if (!ValidationUtil.isValidRa(ra)) {
                    System.err.println("Linha " + lineNumber + " ignorada (RA inválido): " + ra);
                    continue;
                }
                if (!ValidationUtil.isValidCpf(cpf)) {
                    System.err.println("Linha " + lineNumber + " ignorada (CPF inválido): " + cpf);
                    continue;
                }
                if (!ValidationUtil.isValidEmail(email)) {
                    System.err.println("Linha " + lineNumber + " ignorada (e-mail inválido): " + email);
                    continue;
                }

                // Monta o objeto Student com os dados validados.
                Student student = new Student();
                student.setName(name);
                student.setRa(ra);
                student.setCpf(cpf);
                student.setEmail(email);
                student.setEligible(true); // Aluno importado já entra como "apto" por padrão.

                students.add(student); // Adiciona à lista de resultado.
            }
        }

        return students; // Devolve todos os alunos válidos lidos do arquivo.
    }
}
