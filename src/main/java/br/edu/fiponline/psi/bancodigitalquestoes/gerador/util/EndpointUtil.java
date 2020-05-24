package br.edu.fiponline.psi.bancodigitalquestoes.gerador.util;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.exception.ResourceNotFoundException;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.ApplicationUser;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Professor;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Student;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class EndpointUtil implements Serializable {
    public ResponseEntity<?> returnObjectOrNotFound(Object object) {
        if (object == null) throw new ResourceNotFoundException("Não encontrado");
        return new ResponseEntity<>(object, HttpStatus.OK);
    }

    public ResponseEntity<?> returnObjectOrNotFound(List<?> list) {
        if (list == null || list.isEmpty()) throw new ResourceNotFoundException("Não encontrado");
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    public Professor extractProfessorFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((ApplicationUser) authentication.getPrincipal()).getProfessor();
    }

    public Student extractStudentFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((ApplicationUser) authentication.getPrincipal()).getStudent();
    }
}
