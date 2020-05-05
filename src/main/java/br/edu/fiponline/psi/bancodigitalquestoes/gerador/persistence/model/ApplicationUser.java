package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class ApplicationUser extends AbstractEntity {
    @NotEmpty(message = "O campo de usuário não pode ser vazio")
    @Column(unique = true)
    private String username;
    @NotEmpty(message = "O campo de senha não pode ser vazio")
    private String password;
    @OneToOne
    private Professor professor;
    @OneToOne
    private Student student;

    public ApplicationUser() {
    }

    public ApplicationUser(ApplicationUser applicationUser) {
        this.username = applicationUser.username;
        this.password = applicationUser.password;
        this.professor = applicationUser.professor;
        this.student = applicationUser.student;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
