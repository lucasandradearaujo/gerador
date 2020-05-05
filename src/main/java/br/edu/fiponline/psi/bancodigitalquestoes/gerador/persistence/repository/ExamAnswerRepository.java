package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.ExamAnswer;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ExamAnswerRepository extends PagingAndSortingRepository<ExamAnswer, Long> {
    boolean existsExamAnswerByAssignmentIdAndStudentId(long assignmentId, long studentId);
}
