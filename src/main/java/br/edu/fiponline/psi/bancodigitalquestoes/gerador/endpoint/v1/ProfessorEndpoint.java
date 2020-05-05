package br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Professor;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.ProfessorRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/professor")
public class ProfessorEndpoint {
    private final ProfessorRepository professorRepository;

    @Autowired
    public ProfessorEndpoint(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @GetMapping(path = "{id}")
    @ApiOperation(value = "Encontrar professor por seu ID", notes = "Temos que melhorar esse método", response = Professor.class)
    public ResponseEntity<?> getProfessorById(@PathVariable long id) {
        Professor professor = professorRepository.findOne(id);
        return new ResponseEntity<>(professor, HttpStatus.OK);
    }
}
