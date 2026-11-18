package br.com.CurriculoAi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = decodeRequest(request);

        if (token != null){
            tokenService.validateTokenAndGetSubject(token)
                    .ifPresent(this::authenticate);
        }

        filterChain.doFilter(request,response);
    }

    private void authenticate(String email){
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (UsernameNotFoundException e){

            log.warn("Token válido, mas o usuário do subject não existe mais");
        }
    }

    //Metodo responsável por pegar JWT no header Authorization
    public String decodeRequest(HttpServletRequest request){

        String header = request.getHeader("Authorization");

        if (header == null) return null;

        //Tira o Bearer e deixa apenas o JWT
        return header.replace("Bearer ", "");
    }
}