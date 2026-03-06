package br.edu.ufape.personal_trainer.dto;

import br.edu.ufape.personal_trainer.model.Chat;

public record ChatResponse(
    Long id,
    Long alunoId,
    String alunoNome,
    Long personalId,
    String personalNome
) {
    public ChatResponse(Chat c) {
        this(
            c.getChatId(),
            c.getAluno().getUsuarioId(),
            c.getAluno().getNome(),
            c.getPersonal().getUsuarioId(),
            c.getPersonal().getNome()
        );
    }
}