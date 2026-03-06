package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.*;

public record ItemTreinoRequest(
    @NotNull(message = "Exercício é obrigatório")
    Long exercicioId,

    @NotNull(message = "Séries é obrigatório")
    @Min(value = 1, message = "Séries deve ser maior que zero")
    Integer series,

    @NotBlank(message = "Repetições é obrigatório")
    @Pattern(
        regexp = "^(\\d+(-\\d+)?|AMRAP|até falha)$",
        message = "Repetições inválidas. Use: 10, 8-12, AMRAP, até falha"
    )
    String repeticoes,

    @PositiveOrZero(message = "Carga deve ser maior ou igual a zero")
    Double cargaKg,

    @NotNull(message = "Descanso é obrigatório")
    @Min(value = 0, message = "Descanso deve ser maior ou igual a zero")
    Integer descansoSegundos
) {}