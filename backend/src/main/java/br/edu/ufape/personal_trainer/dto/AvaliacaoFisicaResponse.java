package br.edu.ufape.personal_trainer.dto;

import java.time.LocalDate;

import br.edu.ufape.personal_trainer.model.AvaliacaoFisica;

public record AvaliacaoFisicaResponse(
        
        Long avaliacaoId,
        Long alunoId,
        String alunoNome,
        LocalDate dataAvaliacao,
        Double pesoKg,
        Double alturaCm,
        Double percentualGordura,
        String observacoes,
        Boolean feitoPeloPersonal

) {
    public AvaliacaoFisicaResponse(AvaliacaoFisica a) {
        this(
            a.getAvaliacaoId(),
            a.getAluno().getUsuarioId(),
            a.getAluno().getNome(),
            a.getDataAvaliacao(),
            a.getPesoKg(),
            a.getAlturaCm(),
            a.getPercentualGordura(),
            a.getObservacoes(), 
            a.getFeitoPeloPersonal()
        );
    }
}
