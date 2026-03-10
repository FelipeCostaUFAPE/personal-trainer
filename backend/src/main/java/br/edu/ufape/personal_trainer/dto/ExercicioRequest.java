package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.*;

public record ExercicioRequest(
    @NotNull(message = "Grupo muscular é obrigatório")
    Long grupoMuscularId,

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    String nome,

    @Size(max = 500)
    String descricao
) {}