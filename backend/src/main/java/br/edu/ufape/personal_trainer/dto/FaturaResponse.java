package br.edu.ufape.personal_trainer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.edu.ufape.personal_trainer.enums.StatusFatura;
import br.edu.ufape.personal_trainer.model.Fatura;

import java.time.LocalDate;

public record FaturaResponse(
    Long id,
    Long alunoId,
    Double valor,

    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataVencimento,

    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataPagamento,

    StatusFatura status
) {
    public FaturaResponse(Fatura f) {
        this(
            f.getFaturaId(),
            f.getAluno().getUsuarioId(),
            f.getValor(),
            f.getDataVencimento(),
            f.getDataPagamento(),
            f.getStatus()
        );
    }
}