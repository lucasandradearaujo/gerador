package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Course;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Professor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CourseRepository extends PagingAndSortingRepository<Course, Long> {

    Course findByIdAndProfessor(long id, Professor professor);

}