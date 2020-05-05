package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

@SuppressWarnings("ALL")
public interface CourseRepository extends CustomPagingAndSortRepository<Course, Long> {
    @Query("select c from Course c where c.name like %?1% and c.professor = ?#{principal.professor} and c.enabled = true")
    List<Course> listCoursesByName(String name);
}