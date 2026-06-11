package com.portal.service;

import com.portal.dao.StudentDAO;
import com.portal.model.Student;

import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO = new StudentDAO();

    public List<Student> findAll() {
        return null;
    }

    public Student findById(String id) {
        return null;
    }

    public List<Student> findByName(String name) {
        return null;
    }

    public void save(Student student) {
    }

    public void update(Student student) {
    }

    public void delete(String id) {
    }

    public void setEligible(String id, boolean eligible) {
    }
}
