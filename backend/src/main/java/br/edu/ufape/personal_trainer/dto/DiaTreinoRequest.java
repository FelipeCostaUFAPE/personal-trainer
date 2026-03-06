package br.edu.ufape.personal_trainer.dto;

import br.edu.ufape.personal_trainer.enums.DiaSemana;
import jakarta.validation.constraints.NotNull;

public record DiaTreinoRequest(
    @NotNull(message = "Dia da semana é obrigatório")
    DiaSemana diaSemana
) {}