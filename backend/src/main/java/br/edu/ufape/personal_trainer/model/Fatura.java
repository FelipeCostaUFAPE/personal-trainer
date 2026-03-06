package br.edu.ufape.personal_trainer.model;

import java.time.LocalDate;

import br.edu.ufape.personal_trainer.enums.StatusFatura;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Fatura {
	@Id
	@GeneratedValue
	private Long faturaId;
	
	@ManyToOne
	@JoinColumn(name = "alunoId")
	private Aluno aluno;
	
	@Column(name = "data_vencimento", nullable = false)
	private LocalDate dataVencimento;
	
	@Column(name = "data_pagamento", nullable = true)
	private LocalDate dataPagamento;
	
	@Column(nullable = false)
	private Double valor;
	
	@Enumerated(EnumType.STRING)
	private StatusFatura status = StatusFatura.PENDENTE;
}
