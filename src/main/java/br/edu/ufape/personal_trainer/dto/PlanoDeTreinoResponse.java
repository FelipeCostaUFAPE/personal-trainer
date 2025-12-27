package br.edu.ufape.personal_trainer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.edu.ufape.personal_trainer.model.PlanoDeTreino;

import java.time.LocalDate;

public record PlanoDeTreinoResponse(
    Long id,
    Long alunoId,
    String nome,

    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataInicio,

    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataFim
) {
    public PlanoDeTreinoResponse(PlanoDeTreino p) {
        this(
            p.getPlanoId(),
            p.getAluno().getUsuarioId(),
            p.getNome(),
            p.getDataInicio(),
            p.getDataFim()
        );
    }
}