package com.portal.service;

import com.portal.dao.StudentDAO;
import com.portal.dao.UserDAO;
import com.portal.model.Student;
import com.portal.util.FileImportUtil;
import com.portal.util.ValidationUtil;

import java.util.List;

public class StudentService {

    private final StudentDAO dao = new StudentDAO();
    private final UserDAO userDAO = new UserDAO();

    public List<Student> listar() {
        return dao.findAll();
    }

    public List<Student> buscar(String termo) {
        if (termo == null || termo.isBlank()) return dao.findAll();
        return dao.findByTerm(termo.trim());
    }

    public void criar(Student student) throws ServiceException {
        validar(student, null);
        if (userDAO.existsByEmail(student.getEmail())) {
            throw new ServiceException("Já existe um usuário com esse e-mail.");
        }
        try {
            dao.saveWithUser(student);
        } catch (Exception e) {
            throw new ServiceException("Erro ao salvar aluno: " + e.getMessage());
        }
    }

    public void editar(Student student) throws ServiceException {
        validar(student, student.getId());
        try {
            dao.update(student);
        } catch (Exception e) {
            throw new ServiceException("Erro ao atualizar aluno: " + e.getMessage());
        }
    }

    public void toggleEligivel(Student student) throws ServiceException {
        try {
            dao.toggleEligible(student.getId());
            student.setEligible(!student.isEligible());
        } catch (Exception e) {
            throw new ServiceException("Erro ao alterar aptidão: " + e.getMessage());
        }
    }

    public List<Student> importar(String filePath) throws ServiceException {
        List<Student> importados;
        try {
            importados = FileImportUtil.parseStudents(filePath);
        } catch (Exception e) {
            throw new ServiceException("Erro ao ler arquivo: " + e.getMessage());
        }

        int salvos = 0;
        StringBuilder erros = new StringBuilder();
        for (Student s : importados) {
            try {
                if (dao.existsByRa(s.getRa(), null)) {
                    erros.append("RA ").append(s.getRa()).append(" já cadastrado.\n");
                    continue;
                }
                if (dao.existsByCpf(s.getCpf(), null)) {
                    erros.append("CPF ").append(s.getCpf()).append(" já cadastrado.\n");
                    continue;
                }
                dao.saveWithUser(s);
                salvos++;
            } catch (Exception e) {
                erros.append("Erro no aluno ").append(s.getName()).append(": ").append(e.getMessage()).append("\n");
            }
        }

        if (salvos == 0 && !importados.isEmpty()) {
            throw new ServiceException("Nenhum aluno foi importado.\n" + erros);
        }
        return importados.subList(0, salvos);
    }

    private void validar(Student s, String excludeId) throws ServiceException {
        if (s.getName() == null || s.getName().isBlank())
            throw new ServiceException("Nome é obrigatório.");
        if (!ValidationUtil.isValidRa(s.getRa()))
            throw new ServiceException("RA inválido.");
        if (!ValidationUtil.isValidCpf(s.getCpf()))
            throw new ServiceException("CPF inválido.");
        if (dao.existsByRa(s.getRa(), excludeId))
            throw new ServiceException("Já existe um aluno com esse RA.");
        if (dao.existsByCpf(s.getCpf(), excludeId))
            throw new ServiceException("Já existe um aluno com esse CPF.");
    }
}
