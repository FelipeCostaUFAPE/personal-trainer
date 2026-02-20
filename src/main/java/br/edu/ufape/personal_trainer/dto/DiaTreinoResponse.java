package br.edu.ufape.personal_trainer.dto;

import java.util.List;

import br.edu.ufape.personal_trainer.enums.DiaSemana;
import br.edu.ufape.personal_trainer.model.DiaTreino;

public record DiaTreinoResponse(
	    Long id,
	    DiaSemana diaSemana,
	    Long planoId,
	    List<ItemTreinoResponse> itens
	) {
	    public DiaTreinoResponse(DiaTreino dia) {
	        this(
	            dia.getId(),
	            dia.getDiaSemana(),
	            dia.getPlano().getPlanoId(),
	            dia.getItens().stream().map(ItemTreinoResponse::new).toList()
	        );
	    }
	}