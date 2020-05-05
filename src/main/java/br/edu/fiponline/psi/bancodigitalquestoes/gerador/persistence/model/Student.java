package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Student extends AbstractEntity {
    @NotEmpty(message = "O campo de nome não pode ser vazio")
    private String name;
    @Email(message = "O email não é válido")
    @NotEmpty(message = "O campo de email não pode ser vazio")
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

    public static final class Builder {
        private Student student;

        private Builder() {
            student = new Student();
        }

        public static Builder newStudent() {
            return new Builder();
        }

        public Builder id(Long id) {
            student.setId(id);
            return this;
        }

        public Builder name(String name) {
            student.setName(name);
            return this;
        }

        public Builder enabled(boolean enabled) {
            student.setEnabled(enabled);
            return this;
        }

        public Builder email(String email) {
            student.setEmail(email);
            return this;
        }

        public Student build() {
            return student;
        }
    }
}
