package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Entity
public class Professor extends EntidadeAbstrata{
    @NotEmpty(message = "O campo de nome não pode ser vazio")
    private String nome;
    @Email(message = "O email não é válido")
    @NotEmpty(message = "O campo de email não pode ser vazio")
    @Column(unique = true)
    private String email;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
