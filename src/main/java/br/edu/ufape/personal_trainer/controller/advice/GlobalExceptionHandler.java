package br.edu.ufape.personal_trainer.controller.advice;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return buildResponse(400, "Erro de validação", "Dados inválidos", request.getRequestURI(), errors);
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessValidation(
            BusinessValidationException ex, HttpServletRequest request) {
        return buildResponse(400, "Validação de negócio", "Dados inválidos", request.getRequestURI(), ex.getErrors());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> handleBusinessRule(Exception ex, HttpServletRequest request) {
        Map<String, String> detail = new HashMap<>();
        detail.put("erro", ex.getMessage());
        return buildResponse(400, "Regra de negócio", ex.getMessage(), request.getRequestURI(), detail);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(500, "Erro inesperado", ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalError(Exception ex, HttpServletRequest request) {
        return buildResponse(500, "Erro interno", "Ocorreu um erro inesperado", request.getRequestURI(), null);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            int status, String error, String message, String path, Map<String, String> detail) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(Instant.now());
        response.setStatus(status);
        response.setError(error);
        response.setMessage(message);
        response.setPath(path);
        response.setDetail(detail);
        return ResponseEntity.status(status).body(response);
    }
}