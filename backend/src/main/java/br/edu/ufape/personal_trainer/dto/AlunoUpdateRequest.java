package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

public record AlunoUpdateRequest(
    @Size(min = 4, max = 50, message = "Nome deve ter entre 4 e 50 caracteres")
    String nome,

    @Past(message = "Data deve ser no passado")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataNascimento,

    String modalidade,

    String objetivo
) {}