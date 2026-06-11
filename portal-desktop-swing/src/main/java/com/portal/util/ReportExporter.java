package com.portal.util;

import com.portal.model.Application;
import com.portal.model.Company;
import com.portal.model.Job;
import com.portal.model.Student;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportExporter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static void exportCompanies(List<Company> companies, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("=".repeat(60));
            writer.newLine();
            writer.write("RELATÓRIO DE EMPRESAS — Portal de Estágios UniALFA");
            writer.newLine();
            writer.write("Gerado em: " + LocalDateTime.now().format(FORMATTER));
            writer.newLine();
            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();

            for (Company c : companies) {
                writer.write("Nome:   " + c.getName());
                writer.newLine();
                writer.write("CNPJ:   " + c.getCnpj());
                writer.newLine();
                writer.write("Status: " + c.getStatus());
                writer.newLine();
                writer.write("Fone:   " + (c.getPhone() != null ? c.getPhone() : "-"));
                writer.newLine();
                writer.write("-".repeat(40));
                writer.newLine();
            }

            writer.write("Total: " + companies.size() + " empresa(s)");
            writer.newLine();
        }
    }

    public static void exportStudents(List<Student> students, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("=".repeat(60));
            writer.newLine();
            writer.write("RELATÓRIO DE ALUNOS — Portal de Estágios UniALFA");
            writer.newLine();
            writer.write("Gerado em: " + LocalDateTime.now().format(FORMATTER));
            writer.newLine();
            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();

            for (Student s : students) {
                writer.write("Nome:  " + s.getName());
                writer.newLine();
                writer.write("RA:    " + s.getRa());
                writer.newLine();
                writer.write("CPF:   " + s.getCpf());
                writer.newLine();
                writer.write("Apto:  " + (s.isEligible() ? "Sim" : "Não"));
                writer.newLine();
                writer.write("-".repeat(40));
                writer.newLine();
            }

            writer.write("Total: " + students.size() + " aluno(s)");
            writer.newLine();
        }
    }

    public static void exportJobs(List<Job> jobs, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("=".repeat(60));
            writer.newLine();
            writer.write("RELATÓRIO DE VAGAS — Portal de Estágios UniALFA");
            writer.newLine();
            writer.write("Gerado em: " + LocalDateTime.now().format(FORMATTER));
            writer.newLine();
            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();

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
                writer.write("Salário:     " + (j.getSalary() != null ? "R$ " + j.getSalary() : "Não informado"));
                writer.newLine();
                writer.write("-".repeat(40));
                writer.newLine();
            }

            writer.write("Total: " + jobs.size() + " vaga(s)");
            writer.newLine();
        }
    }

    public static void exportApplications(List<Application> applications, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("=".repeat(60));
            writer.newLine();
            writer.write("RELATÓRIO DE CANDIDATURAS — Portal de Estágios UniALFA");
            writer.newLine();
            writer.write("Gerado em: " + LocalDateTime.now().format(FORMATTER));
            writer.newLine();
            writer.write("=".repeat(60));
            writer.newLine();
            writer.newLine();

            for (Application a : applications) {
                writer.write("ID Candidatura: " + a.getId());
                writer.newLine();
                writer.write("Aluno ID:       " + a.getStudentId());
                writer.newLine();
                writer.write("Vaga ID:        " + a.getJobId());
                writer.newLine();
                writer.write("Status:         " + a.getStatus());
                writer.newLine();
                writer.write("-".repeat(40));
                writer.newLine();
            }

            writer.write("Total: " + applications.size() + " candidatura(s)");
            writer.newLine();
        }
    }
}
