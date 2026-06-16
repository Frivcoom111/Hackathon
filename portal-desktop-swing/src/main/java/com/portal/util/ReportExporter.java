package com.portal.util;

// Entidades cujos dados serão escritos nos relatórios.
import com.portal.model.Address;
import com.portal.model.Application;
import com.portal.model.Company;
import com.portal.model.Job;
import com.portal.model.Student;

import com.portal.util.ValidationUtil; // Usado para formatar o CNPJ nos relatórios.

import java.io.BufferedWriter; // Escreve no arquivo de forma eficiente.
import java.io.FileWriter;     // Abre/cria o arquivo de texto para escrita.
import java.io.IOException;    // Exceção lançada em caso de erro de escrita.
import java.time.LocalDateTime;                 // Data/hora atual (carimbo do relatório).
import java.time.format.DateTimeFormatter;      // Formata a data/hora de forma legível.
import java.util.List;

/**
 * ReportExporter: utilitário que EXPORTA relatórios em arquivos de texto (.txt).
 *
 * Para cada tipo de entidade (Empresa, Aluno, Vaga, Candidatura) existe um método
 * que recebe a lista de dados e o caminho do arquivo, e escreve um relatório formatado
 * com cabeçalho, registros e um total no final.
 *
 * Todos os métodos seguem o MESMO padrão de escrita, mudando apenas os campos exibidos.
 */
public class ReportExporter {

    // Define o formato da data/hora exibida no cabeçalho dos relatórios (ex.: 16/06/2026 14:30).
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Exporta um relatório com a lista de EMPRESAS.
     *
     * @param companies lista de empresas a incluir no relatório.
     * @param filePath  caminho do arquivo .txt a ser gerado.
     * @throws IOException se houver erro ao escrever o arquivo.
     */
    public static void exportCompanies(List<Company> companies, String filePath) throws IOException {
        // try-with-resources: garante que o arquivo seja fechado automaticamente ao final.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // ----- Cabeçalho do relatório -----
            writer.write("=".repeat(60)); // Linha decorativa de 60 sinais de "=".
            writer.newLine();             // Quebra de linha.
            writer.write("RELATÓRIO DE EMPRESAS — Portal de Estágios UniALFA");
            writer.newLine();
            writer.write("Gerado em: " + LocalDateTime.now().format(FORMATTER)); // Carimbo de data/hora.
            writer.newLine();
            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();

            // ----- Corpo: um bloco para cada empresa da lista -----
            for (Company c : companies) {
                writer.write("Nome:   " + c.getName());
                writer.newLine();
                writer.write("CNPJ:   " + ValidationUtil.formatCnpj(c.getCnpj())); // CNPJ formatado.
                writer.newLine();
                writer.write("Status: " + c.getStatus());
                writer.newLine();
                // Se não houver telefone, mostra "-" no lugar.
                writer.write("Fone:   " + (c.getPhone() != null ? c.getPhone() : "-"));
                writer.newLine();
                // O endereço é opcional; só é escrito se existir.
                if (c.getAddress() != null) {
                    Address a = c.getAddress();
                    // Monta a linha do logradouro, acrescentando o complemento somente se houver.
                    String logradouro = a.getStreet() + ", " + a.getNumber()
                        + (a.getComplement() != null && !a.getComplement().isBlank() ? " — " + a.getComplement() : "");
                    writer.write("End.:   " + logradouro);
                    writer.newLine();
                    writer.write("        " + a.getDistrict() + " — " + a.getCity() + "/" + a.getState()
                        + "  CEP " + a.formatarCep());
                    writer.newLine();
                }
                writer.write("-".repeat(40)); // Separador entre empresas.
                writer.newLine();
            }

            // ----- Rodapé: total de registros -----
            writer.write("Total: " + companies.size() + " empresa(s)");
            writer.newLine();
        }
    }

    /**
     * Exporta um relatório com a lista de ALUNOS.
     * Segue o mesmo padrão de exportCompanies (cabeçalho, bloco por aluno, total).
     */
    public static void exportStudents(List<Student> students, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Cabeçalho.
            writer.write("=".repeat(60));
            writer.newLine();
            writer.write("RELATÓRIO DE ALUNOS — Portal de Estágios UniALFA");
            writer.newLine();
            writer.write("Gerado em: " + LocalDateTime.now().format(FORMATTER));
            writer.newLine();
            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();

            // Um bloco por aluno.
            for (Student s : students) {
                writer.write("Nome:  " + s.getName());
                writer.newLine();
                writer.write("RA:    " + s.getRa());
                writer.newLine();
                writer.write("CPF:   " + s.getCpf());
                writer.newLine();
                writer.write("Apto:  " + (s.isEligible() ? "Sim" : "Não")); // boolean traduzido para Sim/Não.
                writer.newLine();
                if (s.getAddress() != null) { // Endereço opcional.
                    Address a = s.getAddress();
                    String logradouro = a.getStreet() + ", " + a.getNumber()
                        + (a.getComplement() != null && !a.getComplement().isBlank() ? " — " + a.getComplement() : "");
                    writer.write("End.:  " + logradouro);
                    writer.newLine();
                    writer.write("       " + a.getDistrict() + " — " + a.getCity() + "/" + a.getState()
                        + "  CEP " + a.formatarCep());
                    writer.newLine();
                }
                writer.write("-".repeat(40));
                writer.newLine();
            }

            // Total de alunos.
            writer.write("Total: " + students.size() + " aluno(s)");
            writer.newLine();
        }
    }

    /**
     * Exporta um relatório com a lista de VAGAS.
     * Mesmo padrão dos demais; exibe os dados específicos de cada vaga.
     */
    public static void exportJobs(List<Job> jobs, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Cabeçalho.
            writer.write("=".repeat(60));
            writer.newLine();
            writer.write("RELATÓRIO DE VAGAS — Portal de Estágios UniALFA");
            writer.newLine();
            writer.write("Gerado em: " + LocalDateTime.now().format(FORMATTER));
            writer.newLine();
            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();

            // Um bloco por vaga.
            for (Job j : jobs) {
                writer.write("Título:      " + j.getTitle());
                writer.newLine();
                writer.write("Área:        " + j.getArea());
                writer.newLine();
                writer.write("Modalidade:  " + j.getModality());
                writer.newLine();
                writer.write("Status:      " + j.getStatus());
                writer.newLine();
                writer.write("Local:       " + j.getLocation());
                writer.newLine();
                // Mostra o salário com "R$" se houver; senão, "Não informado".
                writer.write("Salário:     " + (j.getSalary() != null ? "R$ " + j.getSalary() : "Não informado"));
                writer.newLine();
                writer.write("-".repeat(40));
                writer.newLine();
            }

            // Total de vagas.
            writer.write("Total: " + jobs.size() + " vaga(s)");
            writer.newLine();
        }
    }

    /**
     * Exporta um relatório com a lista de CANDIDATURAS.
     * Mesmo padrão dos demais; usa os nomes já cruzados (aluno e vaga) quando disponíveis.
     */
    public static void exportApplications(List<Application> applications, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Cabeçalho.
            writer.write("=".repeat(60));
            writer.newLine();
            writer.write("RELATÓRIO DE CANDIDATURAS — Portal de Estágios UniALFA");
            writer.newLine();
            writer.write("Gerado em: " + LocalDateTime.now().format(FORMATTER));
            writer.newLine();
            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();

            // Um bloco por candidatura.
            for (Application a : applications) {
                // Prefere mostrar o NOME do aluno; se não houver, cai para o ID.
                writer.write("Aluno:  " + (a.getStudentName() != null ? a.getStudentName() : a.getStudentId()));
                writer.newLine();
                // Prefere mostrar o TÍTULO da vaga; se não houver, cai para o ID.
                writer.write("Vaga:   " + (a.getJobTitle() != null ? a.getJobTitle() : a.getJobId()));
                writer.newLine();
                writer.write("Status: " + a.getStatus());
                writer.newLine();
                writer.write("-".repeat(40));
                writer.newLine();
            }

            // Total de candidaturas.
            writer.write("Total: " + applications.size() + " candidatura(s)");
            writer.newLine();
        }
    }
}
