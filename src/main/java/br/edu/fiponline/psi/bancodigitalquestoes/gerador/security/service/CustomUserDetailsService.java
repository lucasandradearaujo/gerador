package br.edu.fiponline.psi.bancodigitalquestoes.gerador.security.service;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.AplicacaoUsuario;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final ApplicationUserRepository applicationUserRepository;

    @Autowired
    public CustomUserDetailsService(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AplicacaoUsuario applicationUser = loadApplicationUserByUsername(username);
        return new CustomUserDetails(applicationUser);
    }

    public AplicacaoUsuario loadApplicationUserByUsername(String username) {
        return Optional.ofNullable(applicationUserRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("Aplicação de usuário não encontrado!"));
    }

    private final static class CustomUserDetails extends AplicacaoUsuario implements UserDetails {

        private CustomUserDetails(AplicacaoUsuario aplicacaoUsuario){
            super(aplicacaoUsuario);
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            List<GrantedAuthority> authorityListProfessor = AuthorityUtils.createAuthorityList("ROLE_PROFESSOR");
            List<GrantedAuthority> authorityListEstudante = AuthorityUtils.createAuthorityList("ROLE_ESTUDANTE");
            return this.getProfessor() != null ? authorityListProfessor : authorityListEstudante;
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public String getUsername() {
            return null;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
