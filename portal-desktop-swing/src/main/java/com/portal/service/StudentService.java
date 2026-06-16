package com.portal.service;

import com.portal.dao.StudentDAO;
import com.portal.dao.UserDAO;
import com.portal.model.Student;
import com.portal.util.FileImportUtil;
import com.portal.util.ValidationUtil;

import java.util.List;

/**
 * StudentService: serviço com as REGRAS DE NEGÓCIO dos ALUNOS.
 *
 * Concentra validações (nome, RA, CPF), checagem de duplicidade e o fluxo de
 * importação em massa de alunos a partir de arquivo. A tela sempre conversa com este
 * serviço, nunca direto com o banco.
 */
public class StudentService {

    private final StudentDAO dao = new StudentDAO(); // Acesso aos alunos.
    private final UserDAO userDAO = new UserDAO();   // Usado para checar e-mail duplicado.

    /** Lista todos os alunos. */
    public List<Student> listar() {
        return dao.findAll();
    }

    /**
     * Busca alunos por um termo (nome ou RA).
     * Se o termo for vazio, devolve a lista completa (comportamento de "limpar filtro").
     */
    public List<Student> buscar(String termo) {
        if (termo == null || termo.isBlank()) return dao.findAll();
        return dao.findByTerm(termo.trim());
    }

    /**
     * Cria um novo aluno (com sua conta de usuário).
     * Valida os dados e também garante que o e-mail ainda não está em uso.
     *
     * @throws ServiceException se a validação falhar ou ocorrer erro ao salvar.
     */
    public void criar(Student student) throws ServiceException {
        validar(student, null); // Valida como cadastro novo.
        if (userDAO.existsByEmail(student.getEmail())) {
            throw new ServiceException("Já existe um usuário com esse e-mail.");
        }
        try {
            dao.saveWithUser(student); // Cria User + Student em transação.
        } catch (Exception e) {
            // Converte qualquer erro técnico em uma mensagem de negócio amigável.
            throw new ServiceException("Erro ao salvar aluno: " + e.getMessage());
        }
    }

    /**
     * Edita um aluno existente. Valida os dados (ignorando o próprio aluno nas
     * checagens de duplicidade) e atualiza no banco.
     */
    public void editar(Student student) throws ServiceException {
        validar(student, student.getId());
        try {
            dao.update(student);
        } catch (Exception e) {
            throw new ServiceException("Erro ao atualizar aluno: " + e.getMessage());
        }
    }

    /**
     * Alterna a aptidão do aluno (apto/inapto).
     * Atualiza no banco e também ajusta o objeto em memória para refletir o novo estado.
     */
    public void toggleEligivel(Student student) throws ServiceException {
        try {
            dao.toggleEligible(student.getId());
            student.setEligible(!student.isEligible()); // Inverte o valor atual em memória.
        } catch (Exception e) {
            throw new ServiceException("Erro ao alterar aptidão: " + e.getMessage());
        }
    }

    /**
     * Importa vários alunos a partir de um arquivo de texto.
     *
     * Fluxo: lê o arquivo, e tenta salvar cada aluno. Alunos com RA ou CPF já existentes
     * são pulados (e o motivo é acumulado em "erros"). Ao final:
     *   - se nenhum foi salvo e havia alunos no arquivo, lança erro com o relatório;
     *   - caso contrário, devolve apenas os que foram efetivamente salvos.
     *
     * @return a sublista dos alunos que foram realmente importados.
     * @throws ServiceException se o arquivo não puder ser lido ou se nada for importado.
     */
    public List<Student> importar(String filePath) throws ServiceException {
        List<Student> importados;
        try {
            // Lê e já valida o formato básico de cada linha do arquivo.
            importados = FileImportUtil.parseStudents(filePath);
        } catch (Exception e) {
            throw new ServiceException("Erro ao ler arquivo: " + e.getMessage());
        }

        int salvos = 0;                       // Conta quantos foram salvos com sucesso.
        StringBuilder erros = new StringBuilder(); // Acumula as mensagens de erro/pulos.
        for (Student s : importados) {
            try {
                // Pula quem já tem RA cadastrado.
                if (dao.existsByRa(s.getRa(), null)) {
                    erros.append("RA ").append(s.getRa()).append(" já cadastrado.\n");
                    continue;
                }
                // Pula quem já tem CPF cadastrado.
                if (dao.existsByCpf(s.getCpf(), null)) {
                    erros.append("CPF ").append(s.getCpf()).append(" já cadastrado.\n");
                    continue;
                }
                dao.saveWithUser(s); // Salva o aluno (com conta de usuário).
                salvos++;
            } catch (Exception e) {
                // Erro em um aluno não interrompe a importação dos demais.
                erros.append("Erro no aluno ").append(s.getName()).append(": ").append(e.getMessage()).append("\n");
            }
        }

        // Se havia alunos no arquivo, mas nenhum pôde ser salvo, sinaliza falha geral.
        if (salvos == 0 && !importados.isEmpty()) {
            throw new ServiceException("Nenhum aluno foi importado.\n" + erros);
        }
        // Como os salvos ficam no início da lista, devolve só essa parte (0 até "salvos").
        return importados.subList(0, salvos);
    }

    /**
     * Validação central das regras de um aluno (usada na criação e na edição).
     *
     * @param excludeId id a ignorar nas checagens de duplicidade (o próprio aluno ao
     *                  editar, ou null ao criar).
     * @throws ServiceException assim que encontra a primeira regra violada.
     */
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
