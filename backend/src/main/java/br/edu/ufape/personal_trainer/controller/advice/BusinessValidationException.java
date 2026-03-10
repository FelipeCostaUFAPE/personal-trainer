package br.edu.ufape.personal_trainer.controller.advice;

import java.util.Map;

public class BusinessValidationException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Map<String, String> errors;

    public BusinessValidationException(Map<String, String> errors) {
        super("Erros de validação de negócio");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}