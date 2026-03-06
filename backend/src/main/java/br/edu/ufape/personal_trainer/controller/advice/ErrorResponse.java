package br.edu.ufape.personal_trainer.controller.advice;

import lombok.Data;
import java.time.Instant;
import java.util.Map;

@Data
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> detail;
}