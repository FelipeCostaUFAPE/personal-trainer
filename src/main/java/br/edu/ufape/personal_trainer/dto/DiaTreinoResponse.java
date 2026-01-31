package br.edu.ufape.personal_trainer.dto;

import br.edu.ufape.personal_trainer.enums.DiaSemana;
import br.edu.ufape.personal_trainer.model.DiaTreino;

public record DiaTreinoResponse(
    Long id,
    DiaSemana diaSemana,
    Long planoId
) {
    public DiaTreinoResponse(DiaTreino dia) {
        this(
            dia.getId(),
            dia.getDiaSemana(),
            dia.getPlano().getPlanoId()
        );
    }
}