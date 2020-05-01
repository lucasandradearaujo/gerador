package br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.course;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.ApplicationUser;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Course;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Professor;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.CourseRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/professor/course")
@Api(description = "Operações relacionadas ao curso de professores")
public class CourseEndpoint {
    private final CourseRepository courseRepository;

    @Autowired
    public CourseEndpoint(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @ApiOperation(value = "Retornar um curso com base em seu id", response = Course.class)
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getCourseById(@PathVariable long id, Authentication authentication) {
        Professor professor = ((ApplicationUser) authentication.getPrincipal()).getProfessor();
        return new ResponseEntity<>(courseRepository.findByIdAndProfessor(id, professor), HttpStatus.OK);
    }
}
