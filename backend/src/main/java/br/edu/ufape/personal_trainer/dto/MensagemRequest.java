package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.*;

public record MensagemRequest(
    @NotBlank(message = "Conteúdo da mensagem é obrigatório")
    String conteudo
) {}