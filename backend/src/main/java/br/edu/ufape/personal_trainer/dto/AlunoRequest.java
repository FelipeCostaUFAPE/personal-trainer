package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public record AlunoRequest(
	@NotBlank(message = "Nome é obrigatório")
	@Size(min = 4, max = 50, message = "Nome deve ter entre 4 e 50 caracteres")
	String nome,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 4)
    String senha,

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data deve ser no passado")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataNascimento,

    @NotBlank(message = "Modalidade é obrigatória")
    String modalidade,

    @NotBlank(message = "Objetivo é obrigatório")
    String objetivo
) {}