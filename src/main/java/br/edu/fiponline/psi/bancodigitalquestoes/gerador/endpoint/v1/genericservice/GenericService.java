package br.edu.fiponline.psi.bancodigitalquestoes.gerador.endpoint.v1.genericservice;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.exception.ResourceNotFoundException;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.AbstractEntity;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.CustomPagingAndSortRepository;
import org.springframework.stereotype.Service;

@Service
public class GenericService {

    public <T extends AbstractEntity, ID extends Long> void throwResourceNotFoundIfDoesNotExist(T t, CustomPagingAndSortRepository<T, ID> repository, String msg) {
        if (t == null || t.getId() == null || repository.findOne(t.getId()) == null)
            throw new ResourceNotFoundException(msg);
    }

    public <T extends AbstractEntity, ID extends Long> void throwResourceNotFoundIfDoesNotExist(long id, CustomPagingAndSortRepository<T, ID> repository, String msg) {
        if (id == 0 || repository.findOne(id) == null)
            throw new ResourceNotFoundException(msg);
    }
}
