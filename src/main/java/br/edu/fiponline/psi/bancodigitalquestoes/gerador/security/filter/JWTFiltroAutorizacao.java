package br.edu.fiponline.psi.bancodigitalquestoes.gerador.security.filter;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.AplicacaoUsuario;
import br.edu.fiponline.psi.bancodigitalquestoes.gerador.security.service.CustomUserDetailsService;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static br.edu.fiponline.psi.bancodigitalquestoes.gerador.security.filter.Constantes.*;

public class JWTFiltroAutorizacao extends BasicAuthenticationFilter {

    private final CustomUserDetailsService customUserDetailsService;

    public JWTFiltroAutorizacao(AuthenticationManager authenticationManager,
                                CustomUserDetailsService customUserDetailsService) {
        super(authenticationManager);
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(HEADER_STRING);
        if(header == null | !header.startsWith(TOKEN_PREFIX)){
            chain.doFilter(request,response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(request);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(HttpServletRequest request){
        String token = request.getHeader(HEADER_STRING);
        if(token == null) return null;
        String username = Jwts.parser().setSigningKey(SECRET)
                .parseClaimsJws(token.replace(TOKEN_PREFIX,""))
                .getBody()
                .getSubject();
        UserDetails userDetails =  customUserDetailsService.loadUserByUsername(username);
        AplicacaoUsuario applicationUser = customUserDetailsService.loadApplicationUserByUsername(username);
        return username != null ? new UsernamePasswordAuthenticationToken(applicationUser, null, userDetails.getAuthorities()) : null;
    }
}
