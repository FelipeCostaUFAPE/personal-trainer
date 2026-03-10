package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.*;

public record PersonalUpdateRequest(
    @Size(min = 4, max = 50, message = "Nome deve ter entre 4 e 50 caracteres")
    String nome,

    @Pattern(regexp = "\\d{6}-[A-Z]{2}", message = "CREF deve seguir o formato: 123456-SP")
    String cref
) {}