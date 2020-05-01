package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
public class Professor extends AbstractEntity {
    @NotEmpty(message = "O cmapo de nome não pode ser vazio!")
    private String name;
    @Email(message = "O email não é válido!")
    @NotEmpty(message = "O campo de email não pode ser vazio!")
    @Column(unique = true)
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
