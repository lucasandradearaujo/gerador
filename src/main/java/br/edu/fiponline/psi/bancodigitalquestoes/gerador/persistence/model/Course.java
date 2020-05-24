package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Course extends AbstractEntity {
    @NotEmpty(message = "O campo de nome não pode ser vazio")
    @ApiModelProperty(notes = "Nome do curso")
    private String name;
    @ManyToOne(optional = false)
    private Professor professor;


    public static final class Builder {
        private Course course;

        private Builder() {
            course = new Course();
        }

        public static Builder newCourse() {
            return new Builder();
        }

        public Builder id(Long id) {
            course.setId(id);
            return this;
        }

        public Builder name(String name) {
            course.setName(name);
            return this;
        }

        public Builder professor(Professor professor) {
            course.setProfessor(professor);
            return this;
        }

        public Course build() {
            return course;
        }
    }

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
