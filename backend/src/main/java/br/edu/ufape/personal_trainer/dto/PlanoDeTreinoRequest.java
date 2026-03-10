package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

public record PlanoDeTreinoRequest(
    @NotNull(message = "Aluno é obrigatório")
    Long alunoId,

    @NotBlank(message = "Nome do plano é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    String nome,

    @NotNull(message = "Data de início é obrigatória")
    @FutureOrPresent(message = "Data de início não pode ser no passado")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataInicio,
    
    @NotNull(message = "Data de fim é obrigatória")
    @FutureOrPresent(message = "Data de fim não pode ser no passado")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataFim
) {}