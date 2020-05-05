package br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.course;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.deleteservice.CascadeDeleteService;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.genericservice.GenericService;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Course;
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

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("v1/professor/course")
@Api(description = "Operações relacionadas ao curso de professores")
public class CourseEndpoint {
    private final CourseRepository courseRepository;
    private final GenericService service;
    private final CascadeDeleteService deleteService;
    private final EndpointUtil endpointUtil;

    @Autowired
    public CourseEndpoint(CourseRepository courseRepository, GenericService service,
                          CascadeDeleteService deleteService, EndpointUtil endpointUtil) {
        this.courseRepository = courseRepository;
        this.service = service;
        this.deleteService = deleteService;
        this.endpointUtil = endpointUtil;
    }

    @ApiOperation(value = "Retornar um curso com base em sue id", response = Course.class)
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getCourseById(@PathVariable long id) {
        return endpointUtil.returnObjectOrNotFound(courseRepository.findOne(id));
    }

    @ApiOperation(value = "Retorne uma lista de cursos relacionados ao professor", response = Course.class)
    @GetMapping(path = "list")
    public ResponseEntity<?> listCourses(@ApiParam("Nome do curso") @RequestParam(value = "name", defaultValue = "") String name) {
        return new ResponseEntity<>(courseRepository.listCoursesByName(name), OK);
    }

    @ApiOperation(value = "Exclua um curso específico e todas as perguntas e escolhas relacionadas e retorne 200")
    @DeleteMapping(path = "{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable long id) {
        validateCourseExistenceOnDB(id);
        deleteService.deleteCourseAndAllRelatedEntities(id);
        return new ResponseEntity<>(OK);
    }

    @ApiOperation(value = "Atualize o curso e retorne 200")
    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Course course) {
        validateCourseExistenceOnDB(course.getId());
        courseRepository.save(course);
        return new ResponseEntity<>(OK);
    }

    private void validateCourseExistenceOnDB(Long id) {
        service.throwResourceNotFoundIfDoesNotExist(id, courseRepository, "Curso não encontrado");
    }

    @ApiOperation(value = "Crie um curso e retorne-o")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Course course) {
        course.setProfessor(endpointUtil.extractProfessorFromToken());
        return new ResponseEntity<>(courseRepository.save(course), OK);
    }


}
