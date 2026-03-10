package br.edu.ufape.personal_trainer.dto;

import br.edu.ufape.personal_trainer.model.Exercicio;

public record ExercicioResponse(
    Long id,
    String nome,
    String descricao,
    Long grupoMuscularId,
    String grupoMuscularNome
) {
    public ExercicioResponse(Exercicio e) {
        this(
            e.getExercicioId(),
            e.getNome(),
            e.getDescricao(),
            e.getGrupoMuscular().getGrupoMuscularId(),
            e.getGrupoMuscular().getNome()
        );
    }
}