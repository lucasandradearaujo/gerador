package br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.question;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.deleteservice.CascadeDeleteService;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.genericservice.GenericService;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Question;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.CourseRepository;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.QuestionRepository;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.util.EndpointUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("v1/professor/course/question")
@Api(description = "Operações relacionadas à questão dos cursos")
public class QuestionEndpoint {
    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final GenericService service;
    private final CascadeDeleteService deleteService;
    private final EndpointUtil endpointUtil;

    @Autowired
    public QuestionEndpoint(QuestionRepository questionRepository,
                            CourseRepository courseRepository, GenericService service,
                            CascadeDeleteService deleteService, EndpointUtil endpointUtil) {
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
        this.service = service;
        this.deleteService = deleteService;
        this.endpointUtil = endpointUtil;
    }

    @ApiOperation(value = "Retorne a questão com base em seu id", response = Question.class)
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable long id) {
        return endpointUtil.returnObjectOrNotFound(questionRepository.findOne(id));
    }

    @ApiOperation(value = "Retorne uma lista de questões com base no curso", response = Question[].class)
    @GetMapping(path = "list/{courseId}/")
    public ResponseEntity<?> listQuestions(@PathVariable long courseId,
                                           @ApiParam("Título da questão") @RequestParam(value = "title", defaultValue = "") String title) {
        return new ResponseEntity<>(questionRepository.listQuestionsByCourseAndTitle(courseId, title), OK);
    }

    @ApiOperation(value = "Exclua uma pergunta específica e todas as opções relacionadas e retorne 200")
    @DeleteMapping(path = "{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable long id) {
        validateQuestionExistenceOnDB(id);
        deleteService.deleteQuestionAndAllRelatedEntities(id);
        return new ResponseEntity<>(OK);
    }

    @ApiOperation(value = "Atualizar pergunta e retornar 200")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Question question) {
        validateQuestionExistenceOnDB(question.getId());
        questionRepository.save(question);
        return new ResponseEntity<>(OK);
    }

    private void validateQuestionExistenceOnDB(Long id) {
        service.throwResourceNotFoundIfDoesNotExist(id, questionRepository, "Questão não encontrada");
    }

    @ApiOperation(value = "Crie uma questão e retorne-a", response = Question.class)
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Question question) {
        service.throwResourceNotFoundIfDoesNotExist(question.getCourse(), courseRepository, "Curso não encontrado");
        question.setProfessor(endpointUtil.extractProfessorFromToken());
        return new ResponseEntity<>(questionRepository.save(question), OK);
    }


}
