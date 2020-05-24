package br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.exam;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.deleteservice.CascadeDeleteService;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.genericservice.GenericService;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.exception.ConflictException;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.exception.ResourceNotFoundException;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.*;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.*;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.util.EndpointUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("v1/student/exam")
@Api(description = "Operações para associar perguntas a uma atribuição")
public class ExamEndpoint {
    private final QuestionRepository questionRepository;
    private final QuestionAssignmentRepository questionAssignmentRepository;
    private final ChoiceRepository choiceRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExamAnswerRepository examAnswerRepository;
    private final GenericService service;
    private final CascadeDeleteService deleteService;
    private final EndpointUtil endpointUtil;

    @Autowired
    public ExamEndpoint(QuestionRepository questionRepository,
                        QuestionAssignmentRepository questionAssignmentRepository,
                        ChoiceRepository choiceRepository, AssignmentRepository assignmentRepository,
                        ExamAnswerRepository examAnswerRepository, GenericService service,
                        CascadeDeleteService deleteService,
                        EndpointUtil endpointUtil) {
        this.questionRepository = questionRepository;
        this.questionAssignmentRepository = questionAssignmentRepository;
        this.choiceRepository = choiceRepository;
        this.assignmentRepository = assignmentRepository;
        this.examAnswerRepository = examAnswerRepository;
        this.service = service;
        this.deleteService = deleteService;
        this.endpointUtil = endpointUtil;
    }

    @ApiOperation(value = "Valide se o aluno já respondeu ao exame, retorna 409 se sim, 200 sem corpo, se não")
    @GetMapping(path = "validate/{accessCode}")
    public ResponseEntity<?> validateAccessCode(@PathVariable String accessCode) {
        throwConflictExceptionIfStudentAlreadyAnswered(accessCode);
        return new ResponseEntity<>(OK);
    }

    private void throwConflictExceptionIfStudentAlreadyAnswered(String accessCode) {
        throwResourceNotFoundExceptionIfAccessCodeDoesNotExists(accessCode);
        Assignment assignmentByAccessCode = assignmentRepository.findAssignmentByAccessCode(accessCode);
        boolean studentAlreadyAnswered = examAnswerRepository.existsExamAnswerByAssignmentIdAndStudentId(assignmentByAccessCode.getId(), endpointUtil.extractStudentFromToken().getId());
        if (studentAlreadyAnswered) throw new ConflictException("Este exame já foi respondido");
    }

    @ApiOperation(value = "Listar todas as opções com base nas perguntas pelo código de acesso da atribuição", response = Choice[].class)
    @GetMapping(path = "choice/{accessCode}")
    public ResponseEntity<?> listQuestionsFromQuestionAssignmentByAssignmentAccessCode(@PathVariable String accessCode) {
        throwResourceNotFoundExceptionIfAccessCodeDoesNotExists(accessCode);
        throwConflictExceptionIfStudentAlreadyAnswered(accessCode);
        List<Question> questions = questionAssignmentRepository.listQuestionsFromQuestionAssignmentByAssignmentAccessCode(accessCode);
        List<Long> questionsId = questions.stream().map(Question::getId).collect(Collectors.toList());
        List<Choice> choices = choiceRepository.listChoicesByQuestionsIdForStudent(questionsId);
        return new ResponseEntity<>(choices, OK);
    }

    private void throwResourceNotFoundExceptionIfAccessCodeDoesNotExists(String accessCode) {
        Assignment assignment = assignmentRepository.findAssignmentByAccessCode(accessCode);
        if (assignment == null)
            throw new ResourceNotFoundException("Código de Acesso Inválido");
    }

    @ApiOperation(value = "Salvar as respostas dos alunos")
    @PostMapping(path = "{accessCode}")
    @Transactional
    public ResponseEntity<?> save(@PathVariable String accessCode, @RequestBody Map<Long, Long> questionChoiceIdsMap) {
        Assignment assignment = assignmentRepository.findAssignmentByAccessCode(accessCode);
        if (assignment == null) throw new ResourceNotFoundException("Código de Acesso Inválido");
        internallySaveExamAnswer(questionChoiceIdsMap, assignment);
        return new ResponseEntity<>(OK);
    }

    private void internallySaveExamAnswer(Map<Long, Long> questionChoiceIdsMap, Assignment assignment) {
        questionChoiceIdsMap.forEach((questionId, choiceId) -> {
            QuestionAssignment questionAssignment = questionAssignmentRepository.findQuestionAssignmentByAssignmentIdAndQuestionId(assignment.getId(), questionId);
            Choice selectedChoiceByStudent = choiceRepository.findOne(choiceId);
            Choice correctChoice = choiceRepository.findCorrectChoiceForQuestion(questionId);
            ExamAnswer examAnswer = ExamAnswer.ExamAnswerBuilder.newExamAnswer()
                    .questionId(questionId)
                    .assignmentId(assignment.getId())
                    .questionAssignmentId(questionAssignment.getId())
                    .professorId(assignment.getProfessor().getId())
                    .studentId(endpointUtil.extractStudentFromToken().getId())
                    .assignmentTitle(assignment.getTitle())
                    .questionTitle(questionAssignment.getQuestion().getTitle())
                    .choiceGrade(questionAssignment.getGrade())
                    .answerGrade(selectedChoiceByStudent.isCorrectAnswer() ? questionAssignment.getGrade() : 0)
                    .selectedChoiceId(selectedChoiceByStudent.getId())
                    .selectedChoiceTitle(selectedChoiceByStudent.getTitle())
                    .correctChoiceId(correctChoice.getId())
                    .correctChoiceTitle(correctChoice.getTitle())
                    .build();
            examAnswerRepository.save(examAnswer);
        });
    }


}

