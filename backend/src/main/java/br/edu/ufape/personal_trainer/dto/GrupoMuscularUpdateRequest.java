package br.edu.ufape.personal_trainer.dto;

import jakarta.validation.constraints.Size;

public record GrupoMuscularUpdateRequest(
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    String nome,

    @Size(max = 100, message = "Descrição com no maximo 100 caracteres")
    String descricao
) {}