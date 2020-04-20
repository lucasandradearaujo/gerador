package br.edu.fiponline.psi.bancodigitalquestoes.gerador.pontofinalv1;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/professor")
public class ProfessorPontoFinal {

    @GetMapping
    public ResponseEntity<?> ola (){
        return new ResponseEntity<>("Olá!", HttpStatus.OK);
    }
}
