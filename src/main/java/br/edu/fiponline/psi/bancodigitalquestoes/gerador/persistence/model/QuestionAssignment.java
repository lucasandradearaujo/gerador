package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@ApiModel(description = "Esta será a turma responsável por gerar o exame para os alunos," +
        "A soma de todas as perguntas deve ser igual a 100")
public class QuestionAssignment extends AbstractEntity {
    @ManyToOne(optional = false)
    private Professor professor;
    @ManyToOne
    @ApiModelProperty(notes = "A pergunta com pelo menos duas opções e uma delas é verdadeira")
    private Question question;
    @ManyToOne
    @ApiModelProperty(notes = "A tarefa a que esta pergunta pertence")
    private Assignment assignment;
    @ApiModelProperty(notes = "O valor da nota para a pergunta")
    private double grade;


    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public static final class Builder {
        private QuestionAssignment questionAssignment;

        private Builder() {
            questionAssignment = new QuestionAssignment();
        }

        public static Builder newQuestionAssignment() {
            return new Builder();
        }

        public Builder id(Long id) {
            questionAssignment.setId(id);
            return this;
        }

        public Builder enabled(boolean enabled) {
            questionAssignment.setEnabled(enabled);
            return this;
        }

        public Builder professor(Professor professor) {
            questionAssignment.setProfessor(professor);
            return this;
        }

        public Builder question(Question question) {
            questionAssignment.setQuestion(question);
            return this;
        }

        public Builder assignment(Assignment assignment) {
            questionAssignment.setAssignment(assignment);
            return this;
        }

        public Builder grade(double grade) {
            questionAssignment.setGrade(grade);
            return this;
        }

        public QuestionAssignment build() {
            return questionAssignment;
        }
    }
}
