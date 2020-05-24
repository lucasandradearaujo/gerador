package br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.assignment;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.deleteservice.CascadeDeleteService;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.genericservice.GenericService;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Assignment;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.AssignmentRepository;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.CourseRepository;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.util.EndpointUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("v1/professor/course/assignment")
@Api(description = "Operações relacionadas à atribuição de cursos")
public class AssignmentEndpoint {
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final GenericService service;
    private final EndpointUtil endpointUtil;
    private final CascadeDeleteService deleteService;

    @Autowired
    public AssignmentEndpoint(AssignmentRepository assignmentRepository,
                              CourseRepository courseRepository, GenericService service,
                              EndpointUtil endpointUtil, CascadeDeleteService deleteService) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.service = service;
        this.endpointUtil = endpointUtil;
        this.deleteService = deleteService;
    }

    @ApiOperation(value = "Retornar uma tarefa com base em seu ID", response = Assignment.class)
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getAssignmentById(@PathVariable long id) {
        return endpointUtil.returnObjectOrNotFound(assignmentRepository.findOne(id));
    }

    @ApiOperation(value = "Retornar uma lista de tarefas relacionadas ao curso", response = Assignment[].class)
    @GetMapping(path = "list/{courseId}/")
    public ResponseEntity<?> listAssignments(@PathVariable long courseId,
                                             @ApiParam("Assignment title") @RequestParam(value = "title", defaultValue = "") String title) {
        return new ResponseEntity<>(assignmentRepository.listAssignemntsByCourseAndTitle(courseId, title), OK);
    }

    @ApiOperation(value = "Excluir uma tarefa específica retornar 200 Ok sem corpo")
    @DeleteMapping(path = "{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable long id) {
        validateAssignmentExistenceOnDB(id);
        assignmentRepository.delete(id);
        deleteService.deleteAssignmentAndAllRelatedEntities(id);
        return new ResponseEntity<>(OK);
    }

    @ApiOperation(value = "Atualizar atribuição e devolver 200 Ok sem corpo")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Assignment assignment) {
        validateAssignmentExistenceOnDB(assignment.getId());
        assignmentRepository.save(assignment);
        return new ResponseEntity<>(OK);
    }

    private void validateAssignmentExistenceOnDB(Long id) {
        service.throwResourceNotFoundIfDoesNotExist(id, assignmentRepository, "Tarefa não encontrada");
    }

    @ApiOperation(value = "Crie uma tarefa e retorne a tarefa criada")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Assignment assignment) {
        service.throwResourceNotFoundIfDoesNotExist(assignment.getCourse(), courseRepository, "Curso não encontrado");
        assignment.setProfessor(endpointUtil.extractProfessorFromToken());
        assignment.setAccessCode(generateAccessCode(assignment.getCourse().getId()));
        return new ResponseEntity<>(assignmentRepository.save(assignment), OK);
    }

    private String generateAccessCode(long courseId) {
        long accessCode = ThreadLocalRandom.current().nextLong(1000, 10000);
        while (assignmentRepository.accessCodeExistsForCourse(String.valueOf(accessCode), courseId) != null) {
            generateAccessCode(courseId);
        }
        return String.valueOf(accessCode);
    }


}
