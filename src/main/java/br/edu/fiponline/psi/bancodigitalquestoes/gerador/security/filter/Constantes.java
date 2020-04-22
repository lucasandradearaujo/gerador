package br.edu.fiponline.psi.bancodigitalquestoes.gerador.security.filter;

import java.util.concurrent.TimeUnit;

public class Constantes {
    public static final String SECRET = "secre";
    public static final String TOKEN_PREFIX = "Titular";
    public static final String HEADER_STRING = "Autorização";
    public static final long EXPIRATION_TIME = 86400000L; //1 dia

    public static void main(String[] args) {
        System.out.println(TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
    }
}
