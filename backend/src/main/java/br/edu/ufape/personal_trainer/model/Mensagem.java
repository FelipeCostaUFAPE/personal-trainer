package br.edu.ufape.personal_trainer.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
public class Mensagem {
    @Id
    @GeneratedValue
    private Long mensagemId;

    @ManyToOne
    @JoinColumn(name = "chatId")
    private Chat chat;

    @Column(nullable = false)
    private String conteudo;

    @Column(nullable = false)
    private LocalDateTime timeStamp;

    @Column(nullable = false)
    private Boolean enviadoPeloAluno;
}