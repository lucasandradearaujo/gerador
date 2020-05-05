package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Assignment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@SuppressWarnings("ALL")
public interface AssignmentRepository extends CustomPagingAndSortRepository<Assignment, Long> {
    @Query("select a from Assignment a where a.course.id = ?1 and a.title like %?2% and a.professor = ?#{principal.professor} and a.enabled = true")
    List<Assignment> listAssignemntsByCourseAndTitle(long courseId, String title);

    @Query("update Assignment a set a.enabled = false where a.course.id = ?1 and a.professor = ?#{principal.professor} and a.enabled = true")
    @Modifying
    void deleteAllAssignmentsRelatedToCourse(long courseId);

    @Query("select a from Assignment a where a.course.id = ?1 and a.accessCode =?2 and a.professor = ?#{principal.professor} and a.enabled = true")
    Assignment accessCodeExistsForCourse(String accessCode, long courseId);

    @Query("select a from Assignment a where a.accessCode =?1 and a.enabled = true")
    Assignment findAssignmentByAccessCode(String accessCode);
}