package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model;


import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;

@Entity
public class Course extends AbstractEntity {
    @NotEmpty(message = "O campo nome não pode ser vazio")
    @ApiModelProperty(notes = "O nome do curso")
    private String name;
    @ManyToOne(optional = false)
    private Professor professor;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
}
