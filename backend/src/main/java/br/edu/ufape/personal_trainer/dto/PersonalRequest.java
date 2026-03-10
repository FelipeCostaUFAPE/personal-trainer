package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.*;

public record PersonalRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 4, max = 50, message = "Nome deve ter entre 4 e 50 caracteres")
    String nome,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 4, message = "Senha deve ter no mínimo 4 caracteres")
    String senha,

    @NotBlank(message = "CREF é obrigatório")
    @Pattern(regexp = "\\d{6}-[A-Z]{2}", message = "CREF deve seguir o formato: 123456-SP")
    String cref
) {}