package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.*;

public record ChatRequest(
    @NotNull(message = "Aluno é obrigatório")
    Long alunoId,

    @NotNull(message = "Personal é obrigatório")
    Long personalId
) {}