package com.portal.service;

import com.portal.dao.CourseDAO;
import com.portal.model.Course;

import java.util.List;

public class CourseService {

    private final CourseDAO dao = new CourseDAO();

    public List<Course> listar() {
        return dao.findAll();
    }

    public void criar(String name, String code, int periods) throws ServiceException {
        validar(name, periods, null);
        Course course = new Course();
        course.setName(name.trim());
        course.setCode(code != null && !code.isBlank() ? code.trim() : null);
        course.setPeriods(periods);
        dao.save(course);
    }

    public void editar(Course course, String name, String code, int periods) throws ServiceException {
        validar(name, periods, course.getId());
        course.setName(name.trim());
        course.setCode(code != null && !code.isBlank() ? code.trim() : null);
        course.setPeriods(periods);
        dao.update(course);
    }

    public void toggleAtivo(Course course) {
        dao.toggleActive(course.getId());
    }

    private void validar(String name, int periods, String excludeId) throws ServiceException {
        if (name == null || name.isBlank())
            throw new ServiceException("O nome do curso é obrigatório.");
        if (periods <= 0)
            throw new ServiceException("O número de semestres deve ser maior que zero.");
        if (dao.existsByName(name.trim(), excludeId))
            throw new ServiceException("Já existe um curso com esse nome.");
    }
}
