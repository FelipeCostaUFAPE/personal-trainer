package br.edu.ufape.personal_trainer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.edu.ufape.personal_trainer.model.Aluno;

import java.time.LocalDate;

public record AlunoResponse(
    Long id,
    String nome,
    String email,
    String modalidade,
    String objetivo,
    Boolean ativo,

    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate dataNascimento,

    Long personalId
) {
    public AlunoResponse(Aluno aluno) {
        this(
            aluno.getUsuarioId(),
            aluno.getNome(),
            aluno.getEmail(),
            aluno.getModalidade(),
            aluno.getObjetivo(),
            aluno.getAtivo(),
            aluno.getDataNascimento(),
            aluno.getPersonal() != null ? aluno.getPersonal().getUsuarioId() : null
        );
    }
}