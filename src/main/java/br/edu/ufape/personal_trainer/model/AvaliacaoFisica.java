package br.edu.ufape.personal_trainer.model;

import java.time.LocalDate;

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
public class AvaliacaoFisica {
	@Id
	@GeneratedValue
	private Long avaliacaoId;
	
	@ManyToOne
	@JoinColumn(name = "alunoId")
	private Aluno aluno;
	
	@Column(name = "data_avaliacao", nullable = false)
	private LocalDate dataAvaliacao;
	
	@Column(nullable = false)
	private Double pesoKg;
	
	@Column(nullable = false)
	private Double alturaCm;
	
	@Column(nullable = false)
	private Double percentualGordura;
	
	@Column(nullable = true)
	private String observacoes;
	private Boolean feitoPeloPersonal;
}
