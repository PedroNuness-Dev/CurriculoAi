package br.com.CurriculoAi.security;

import br.com.CurriculoAi.exceptions.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        ErrorResponse exceptionResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Você não tem permissão para acessar este recurso",
                request
        );

        new ObjectMapper().findAndRegisterModules().writeValue(response.getOutputStream(), exceptionResponse);
    }
}
