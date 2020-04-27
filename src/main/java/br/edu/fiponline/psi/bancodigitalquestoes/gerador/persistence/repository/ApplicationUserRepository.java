package br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.AplicacaoUsuario;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ApplicationUserRepository extends PagingAndSortingRepository<AplicacaoUsuario, Long> {
    AplicacaoUsuario findByUsername(String username);
}
