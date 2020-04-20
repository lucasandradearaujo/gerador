package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotEmpty;

@Entity
public class AplicacaoUsuario extends EntidadeAbstrata{
    @NotEmpty(message = "O campo de usuário não pode ser vazio")
    @Column(unique = true)
    private String usuario;
    @NotEmpty(message = "O campo de senha não pode ser vazio")
    private String senha;
    @OneToOne
    private Professor professor;

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
}
