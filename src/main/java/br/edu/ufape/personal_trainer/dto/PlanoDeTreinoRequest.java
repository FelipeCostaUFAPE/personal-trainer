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
    @Future(message = "Data inicio deve ser no futuro")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataInicio,
    
    @NotNull(message = "Data de fim é obrigatória")
    @Future(message = "Data fim deve ser no futuro")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataFim
) {}