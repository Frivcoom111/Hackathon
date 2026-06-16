package com.portal.service;

import com.portal.dao.CourseDAO;
import com.portal.model.Course;

import java.util.List;

/**
 * CourseService: serviço com as REGRAS DE NEGÓCIO dos CURSOS.
 *
 * Fica entre a tela e o CourseDAO. A tela nunca fala direto com o banco: ela chama
 * o serviço, que valida os dados (regras) e só então aciona o DAO. Isso mantém a
 * validação centralizada e a interface mais simples.
 */
public class CourseService {

    private final CourseDAO dao = new CourseDAO(); // DAO usado para o acesso ao banco.

    /** Lista todos os cursos cadastrados. */
    public List<Course> listar() {
        return dao.findAll();
    }

    /**
     * Cria um novo curso, após validar os dados.
     *
     * @throws ServiceException se alguma regra for violada (nome vazio, períodos
     *         inválidos ou nome duplicado).
     */
    public void criar(String name, String code, int periods) throws ServiceException {
        validar(name, periods, null); // null em excludeId: é um curso novo.
        Course course = new Course();
        course.setName(name.trim()); // .trim() remove espaços extras nas pontas.
        // O código é opcional: se vier vazio/em branco, guarda null em vez de "".
        course.setCode(code != null && !code.isBlank() ? code.trim() : null);
        course.setPeriods(periods);
        dao.save(course);
    }

    /**
     * Edita um curso existente, após validar os novos dados.
     * Passa o id do próprio curso em excludeId, para a checagem de nome duplicado
     * não considerar o próprio curso como duplicata.
     */
    public void editar(Course course, String name, String code, int periods) throws ServiceException {
        validar(name, periods, course.getId());
        course.setName(name.trim());
        course.setCode(code != null && !code.isBlank() ? code.trim() : null);
        course.setPeriods(periods);
        dao.update(course);
    }

    /** Ativa ou desativa um curso (alterna o estado atual). */
    public void toggleAtivo(Course course) {
        dao.toggleActive(course.getId());
    }

    /**
     * Validação central das regras de um curso. É "private" porque só é usada
     * internamente, tanto na criação quanto na edição.
     *
     * @param excludeId id a ignorar na checagem de nome duplicado (o próprio curso ao editar,
     *                  ou null ao criar).
     * @throws ServiceException se alguma regra não for atendida.
     */
    private void validar(String name, int periods, String excludeId) throws ServiceException {
        if (name == null || name.isBlank())
            throw new ServiceException("O nome do curso é obrigatório.");
        if (periods <= 0)
            throw new ServiceException("O número de semestres deve ser maior que zero.");
        if (dao.existsByName(name.trim(), excludeId))
            throw new ServiceException("Já existe um curso com esse nome.");
    }
}
