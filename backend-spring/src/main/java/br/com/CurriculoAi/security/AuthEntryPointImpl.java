package br.com.CurriculoAi.security;

import br.com.CurriculoAi.exceptions.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthEntryPointImpl implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ErrorResponse exceptionResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Não autenticado ou credenciais inválidas",
                request
        );

        new ObjectMapper().findAndRegisterModules().writeValue(response.getOutputStream(),exceptionResponse);
    }
}
