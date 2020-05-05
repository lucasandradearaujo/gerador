package br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.questionassignment;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.deleteservice.CascadeDeleteService;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.genericservice.GenericService;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Choice;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Question;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.QuestionAssignment;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.AssignmentRepository;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.QuestionAssignmentRepository;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.QuestionRepository;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.util.EndpointUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("v1/professor/course/assignment/questionassignment")
@Api(description = "Operações para associar perguntas a uma atribuição")
public class QuestionAssignmentEndpoint {
    private final QuestionRepository questionRepository;
    private final QuestionAssignmentRepository questionAssignmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final GenericService service;
    private final CascadeDeleteService deleteService;
    private final EndpointUtil endpointUtil;

    @Autowired
    public QuestionAssignmentEndpoint(QuestionRepository questionRepository,
                                      QuestionAssignmentRepository questionAssignmentRepository,
                                      AssignmentRepository assignmentRepository,
                                      GenericService service,
                                      CascadeDeleteService deleteService,
                                      EndpointUtil endpointUtil) {
        this.questionRepository = questionRepository;
        this.questionAssignmentRepository = questionAssignmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.service = service;
        this.deleteService = deleteService;
        this.endpointUtil = endpointUtil;
    }

    @ApiOperation(value = "Retornar perguntas válidas para esse curso (perguntas válidas são perguntas com pelo menos duas opções" +
            " e uma das opções estiver correta e ainda não estiver associada a essa tarefa)", response = Question[].class)
    @GetMapping(path = "{courseId}/{assignmentId}")
    public ResponseEntity<?> listValidQuestionsForAnAssignment(@PathVariable long courseId, @PathVariable long assignmentId) {
        List<Question> questions = questionRepository.listQuestionsByCourseNotAssociatedWithAnAssignment(courseId, assignmentId);
        List<Question> validQuestions = questions
                .stream()
                .filter(question -> hasMoreThanOneChoice(question) && hasOnlyOneCorrectAnswer(question))
                .collect(Collectors.toList());
        return new ResponseEntity<>(validQuestions, OK);
    }

    private boolean hasOnlyOneCorrectAnswer(Question question) {
        return question.getChoices().stream().filter(Choice::isCorrectAnswer).count() == 1;
    }

    private boolean hasMoreThanOneChoice(Question question) {
        return question.getChoices() != null && question.getChoices().size() > 1;
    }

    @ApiOperation(value = "Associe uma pergunta a uma atribuição e retorne o QuestionAssignment criado", response = QuestionAssignment[].class)
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody QuestionAssignment questionAssignment) {
        validateQuestionAndAssignmentExistence(questionAssignment);
        if (isQuestionAlreadyAssociatedWithAssignment(questionAssignment)) {
            return new ResponseEntity<>(NOT_MODIFIED);
        }
        questionAssignment.setProfessor(endpointUtil.extractProfessorFromToken());
        return new ResponseEntity<>(questionAssignmentRepository.save(questionAssignment), OK);
    }

    private void validateQuestionAndAssignmentExistence(@Valid @RequestBody QuestionAssignment questionAssignment) {
        service.throwResourceNotFoundIfDoesNotExist(questionAssignment.getQuestion(), questionRepository, "Questão não encontrada");
        service.throwResourceNotFoundIfDoesNotExist(questionAssignment.getAssignment(), assignmentRepository, "Tarefa não encontrada");
    }

    private boolean isQuestionAlreadyAssociatedWithAssignment(QuestionAssignment questionAssignment) {
        long questionId = questionAssignment.getQuestion().getId();
        long assignmentId = questionAssignment.getAssignment().getId();
        List<QuestionAssignment> questionAssignments = questionAssignmentRepository.listQuestionAssignmentByQuestionAndAssignment(questionId, assignmentId);
        return !questionAssignments.isEmpty();
    }

    @ApiOperation(value = "Exclua uma pergunta específica atribuída a uma tarefa e retorne 200")
    @DeleteMapping(path = "{questionAssignmentId}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable long questionAssignmentId) {
        validateQuestionAssignmentOnDB(questionAssignmentId);
        deleteService.deleteQuestionAssignmentAndAllRelatedEntities(questionAssignmentId);
        return new ResponseEntity<>(OK);
    }

    @ApiOperation(value = "Atualize QuestionAssignment e retorne 200")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody QuestionAssignment questionAssignment) {
        validateQuestionAssignmentOnDB(questionAssignment.getId());
        questionAssignmentRepository.save(questionAssignment);
        return new ResponseEntity<>(OK);
    }

    private void validateQuestionAssignmentOnDB(Long questionAssignmentId) {
        service.throwResourceNotFoundIfDoesNotExist(questionAssignmentId, questionAssignmentRepository, "QuestionAssignment not found");

    }

    @ApiOperation(value = "Liste todo QuestionAssignment associado ao assignmentId", response = QuestionAssignment[].class)
    @GetMapping(path = "{assignmentId}")
    public ResponseEntity<?> list(@PathVariable long assignmentId) {
        return new ResponseEntity<>(questionAssignmentRepository.listQuestionAssignmentByAssignmentId(assignmentId), OK);
    }


}

