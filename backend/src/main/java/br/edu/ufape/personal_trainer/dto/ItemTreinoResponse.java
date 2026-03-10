package br.edu.ufape.personal_trainer.dto;

import br.edu.ufape.personal_trainer.model.ItemTreino;

public record ItemTreinoResponse(
    Long id,
    Long diaTreinoId,
    Long exercicioId,
    Integer series,
    String repeticoes,
    Double cargaKg,
    int descansoSegundos
) {
    public ItemTreinoResponse(ItemTreino i) {
        this(
            i.getItemTreinoId(),
            i.getDiaTreino().getId(),
            i.getExercicio().getExercicioId(),
            i.getSeries(),
            i.getRepeticoes(),
            i.getCargaKg(),
            i.getDescansoSegundos()
        );
    }
}