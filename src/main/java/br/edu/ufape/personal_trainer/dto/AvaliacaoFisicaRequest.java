package br.edu.ufape.personal_trainer.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;

public record AvaliacaoFisicaRequest(
        
        @NotNull(message = "O ID do aluno é obrigatório")
        Long alunoId,

        @NotNull(message = "A data da avaliação é obrigatória")
        @Past(message = "Avaliação deve ter sido feita")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataAvaliacao,

        @NotNull 
        @Positive(message = "Peso deve ser maior que zero")
        Double pesoKg,

        @NotNull 
        @Positive(message = "Altura deve ser maior que zero")
        Double alturaCm,

        @NotNull 
        @Positive(message = "Percentual de gordura deve ser maior que zero")
        Double percentualGordura,

        String observacoes,
        Boolean feitoPeloPersonal
) {}
