package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.Size;

public record ExercicioUpdateRequest(
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    String nome,

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    String descricao
) {}