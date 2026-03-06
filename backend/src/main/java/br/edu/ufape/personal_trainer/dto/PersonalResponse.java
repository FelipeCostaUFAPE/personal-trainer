package br.edu.ufape.personal_trainer.dto;

import br.edu.ufape.personal_trainer.model.Personal;

public record PersonalResponse(
    Long id,
    String nome,
    String email,
    String cref
) {
    public PersonalResponse(Personal p) {
        this(
            p.getUsuarioId(),
            p.getNome(),
            p.getEmail(),
            p.getCref()
        );
    }
}