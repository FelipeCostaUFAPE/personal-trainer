package br.edu.ufape.personal_trainer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.edu.ufape.personal_trainer.model.Mensagem;

import java.time.LocalDateTime;

public record MensagemResponse(
	    Long id,
	    String conteudo,
	    
	    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
	    LocalDateTime timeStamp,
	    
	    Boolean enviadoPeloAluno,
	    Long chatId
	) {
	    public MensagemResponse(Mensagem m) {
	        this(
	            m.getMensagemId(),
	            m.getConteudo(),
	            m.getTimeStamp(),
	            m.getEnviadoPeloAluno(),
	            m.getChat().getChatId()
	        );
	    }
	}