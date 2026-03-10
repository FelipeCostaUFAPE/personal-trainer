package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

public record FaturaUpdateRequest(
    @Positive(message = "Valor deve ser maior que zero")
    Double valor,

    @Future(message = "Vencimento deve ser no futuro")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataVencimento
) {}