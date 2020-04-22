package br.edu.fiponline.psi.bancodigitalquestoes.gerador.security.filter;

import br.edu.fiponline.psi.bancodigitalquestoes.gerador.persistence.model.AplicacaoUsuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static br.edu.fiponline.psi.bancodigitalquestoes.gerador.security.filter.Constantes.*;

public class JWTFiltroAutenticacao extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;

    public JWTFiltroAutenticacao(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            AplicacaoUsuario usuario = new ObjectMapper().readValue(request.getInputStream(), AplicacaoUsuario.class);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario.getUsuario(), usuario.getSenha()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        ZonedDateTime expTimeUTC = ZonedDateTime.now(ZoneOffset.UTC).plus(EXPIRATION_TIME, ChronoUnit.MILLIS);
        String token = Jwts.builder().setSubject(((AplicacaoUsuario)authResult.getPrincipal())
                .getUsuario()).setExpiration(Date.from(expTimeUTC.toInstant()))
                .signWith(SignatureAlgorithm.HS256, SECRET).compact();

        token = TOKEN_PREFIX+token;
        String tokenJson =  "{\"token\":"+addQuotes(token)+",\"exp\":"+addQuotes(expTimeUTC.toString())+"}";
        response.getWriter().write(tokenJson);
        response.addHeader("Content-Type", "application/json;charset=UTF-8");
        response.addHeader(HEADER_STRING, token);
    }

    private String addQuotes(String value){
        return new StringBuilder(300).append("\"").append(value).append("\"").toString();
    }
}
