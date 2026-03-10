package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

public record PlanoDeTreinoUpdateRequest(
    @Size(min = 1, max = 100, message = "Nome deve ter entre 1 e 100 caracteres")
    String nome,

    @PastOrPresent(message = "Plano de treino deveria ser criado antes ou agora")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataInicio,

    @Future(message = "Plano de treino deve acabar no futuro")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataFim
) {}