package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

public record FaturaRequest(
    @NotNull(message = "Aluno é obrigatório")
    Long alunoId,

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser maior que zero")
    Double valor,

    @NotNull(message = "Data de vencimento é obrigatória")
    @Future(message = "Vencimento deve ser no futuro")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataVencimento
) {}