package br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.choice;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.genericservice.GenericService;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.exception.ConflictException;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.Choice;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.QuestionAssignment;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.ChoiceRepository;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.QuestionAssignmentRepository;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.QuestionRepository;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.util.EndpointUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("v1/professor/course/question/choice")
@Api(description = "Operações relacionadas à escolha de perguntas")
public class ChoiceEndpoint {
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;
    private final QuestionAssignmentRepository questionAssignmentRepository;
    private final GenericService service;
    private final EndpointUtil endpointUtil;

    @Autowired
    public ChoiceEndpoint(QuestionRepository questionRepository,
                          ChoiceRepository choiceRepository, QuestionAssignmentRepository questionAssignmentRepository, GenericService service,
                          EndpointUtil endpointUtil) {
        this.questionRepository = questionRepository;
        this.choiceRepository = choiceRepository;
        this.questionAssignmentRepository = questionAssignmentRepository;
        this.service = service;
        this.endpointUtil = endpointUtil;
    }
    @ApiOperation(value = "Retornar uma escolha com base em seu ID", response = Choice.class)
    @GetMapping(path = "{id}")
    public ResponseEntity<?> getChoiceById(@PathVariable long id) {
        return endpointUtil.returnObjectOrNotFound(choiceRepository.findOne(id));
    }

    @ApiOperation(value = "Retornar uma lista de opções relacionadas ao questionId", response = Choice[].class)
    @GetMapping(path = "list/{questionId}/")
    public ResponseEntity<?> listChoicesByQuestionId(@PathVariable long questionId) {
        return new ResponseEntity<>(choiceRepository.listChoicesByQuestionId(questionId), OK);
    }

    @ApiOperation(value = "Crie uma escolha e retorne a escolha criada",
            notes = "Se a resposta correta dessa opção for verdadeira, a resposta correta de todas as outras opções relacionadas a esta questão será atualizada para falsa")
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@Valid @RequestBody Choice choice) {
        throwResourceNotFoundExceptionIfQuestionDoesNotExist(choice);
        choice.setProfessor(endpointUtil.extractProfessorFromToken());
        Choice savedChoice = choiceRepository.save(choice);
        updateChangingOtherChoicesCorrectAnswerToFalse(choice);
        return new ResponseEntity<>(savedChoice, OK);
    }

    @ApiOperation(value = "Atualize a escolha e retorne 200 Ok sem corpo",
            notes = "Se a resposta correta dessa opção for verdadeira, a resposta correta de todas as outras opções relacionadas a esta questão será atualizada para falsa")
    @PutMapping
    @Transactional
    public ResponseEntity<?> update(@Valid @RequestBody Choice choice) {
        throwResourceNotFoundExceptionIfQuestionDoesNotExist(choice);
        updateChangingOtherChoicesCorrectAnswerToFalse(choice);
        choiceRepository.save(choice);
        return new ResponseEntity<>(OK);
    }

    @ApiOperation(value = "Exclua uma opção específica e retorne 200 Ok sem corpo")
    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        service.throwResourceNotFoundIfDoesNotExist(id, choiceRepository, "A escolha não foi encontrada");
        Choice choice = choiceRepository.findOne(id);
        throwConflictExceptionIfQuestionIsBeingUsedInAnyAssignment(choice.getQuestion().getId());
        choiceRepository.delete(id);
        return new ResponseEntity<>(OK);
    }

    private void throwConflictExceptionIfQuestionIsBeingUsedInAnyAssignment(long questionId) {
        List<QuestionAssignment> questionAssignments = questionAssignmentRepository.listQuestionAssignmentByQuestionId(questionId);
        if (questionAssignments.isEmpty()) return;
        String assignments = questionAssignments
                .stream()
                .map(qa -> qa.getAssignment().getTitle())
                .collect(Collectors.joining(", "));
        throw new ConflictException("Esta opção não pode ser excluída porque esta pergunta está sendo usada nas seguintes atribuições:" + assignments);
    }

    private void throwResourceNotFoundExceptionIfQuestionDoesNotExist(@Valid @RequestBody Choice choice) {
        service.throwResourceNotFoundIfDoesNotExist(choice.getQuestion(), questionRepository, "A questão relacionada a esta escolha não foi encontrada");
    }

    private void updateChangingOtherChoicesCorrectAnswerToFalse(Choice choice) {
        if (choice.isCorrectAnswer())
            choiceRepository.updateAllOtherChoicesCorrectAnswerToFalse(choice, choice.getQuestion());
    }
}
