package br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Professor;

import static org.junit.Assert.*;

public class ProfessorEndpointTest {
    public static Professor mockProfessor(){
        return Professor.Builder.newProfessor()
                .id(1L)
                .name("Lucas")
                .email("lucasaraujo@si.fiponline.edu.br")
                .build();
    }
}