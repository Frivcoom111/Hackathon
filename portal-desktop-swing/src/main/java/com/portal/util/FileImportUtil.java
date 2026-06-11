package com.portal.util;

import com.portal.model.Student;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileImportUtil {

    // Formato esperado: nome;ra;cpf;email;curso
    public static List<Student> parseStudents(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] fields = line.split(";");
                if (fields.length < 5) {
                    System.err.println("Linha " + lineNumber + " ignorada (campos insuficientes): " + line);
                    continue;
                }

                String name  = fields[0].trim();
                String ra    = fields[1].trim();
                String cpf   = fields[2].trim();
                String email = fields[3].trim();
                String curso = fields[4].trim();

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

                Student student = new Student();
                student.setName(name);
                student.setRa(ra);
                student.setCpf(cpf);
                student.setEligible(true);

                students.add(student);
            }
        }

        return students;
    }
}
