package br.com.CurriculoAi.exceptions;

import feign.FeignException;
import feign.RetryableException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Manipulador de exceções do Feign (consumo de APIs externas).
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class FeignExceptionHandler {

    /**
     * 400 vindo da API externa (ex: ViaCEP com CEP em formato inválido).
     */
    @ExceptionHandler(FeignException.BadRequest.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            FeignException.BadRequest ex,
            HttpServletRequest request) {

        log.warn("Requisição inválida para API externa: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST,
                "CEP em formato inválido", request);
    }

    /**
     * 404 vindo da API externa.
     */
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            FeignException.NotFound ex,
            HttpServletRequest request) {

        log.warn("Recurso não encontrado na API externa: {}", ex.getMessage());

        return buildResponse(HttpStatus.NOT_FOUND,
                "Recurso não encontrado no serviço externo", request);
    }

    /**
     * Timeout ou falha de conexão com a API externa.
     */
    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ErrorResponse> handleRetryable(
            RetryableException ex,
            HttpServletRequest request) {

        log.error("Serviço externo indisponível ou com timeout: {}", ex.getMessage());

        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE,
                "Serviço externo indisponível no momento. Tente novamente mais tarde.", request);
    }

    /**
     * 5xx vindo da API externa.
     */
    @ExceptionHandler(FeignException.InternalServerError.class)
    public ResponseEntity<ErrorResponse> handleInternalServerError(
            FeignException.InternalServerError ex,
            HttpServletRequest request) {

        log.error("Erro interno na API externa: {}", ex.getMessage());

        return buildResponse(HttpStatus.BAD_GATEWAY,
                "Erro no serviço externo. Tente novamente mais tarde.", request);
    }

    /**
     * Qualquer outra FeignException não mapeada acima.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(
            FeignException ex,
            HttpServletRequest request) {

        log.error("Erro ao consumir API externa (status {}): {}", ex.status(), ex.getMessage());

        return buildResponse(HttpStatus.BAD_GATEWAY,
                "Erro ao consumir serviço externo", request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(status.value(), message, request);
        return ResponseEntity.status(status).body(errorResponse);
    }
}