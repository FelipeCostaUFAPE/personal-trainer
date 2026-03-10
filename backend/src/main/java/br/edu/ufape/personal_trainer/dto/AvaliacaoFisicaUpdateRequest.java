package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

public record AvaliacaoFisicaUpdateRequest(
    @PastOrPresent(message = "Avaliação deve ter sido feita")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataAvaliacao,

    @Positive(message = "Peso deve ser maior que zero")
    Double pesoKg,

    @Positive(message = "Altura deve ser maior que zero")
    Double alturaCm,

    @Positive(message = "Percentual de gordura deve ser maior que zero")
    Double percentualGordura,

    String observacoes
) {}