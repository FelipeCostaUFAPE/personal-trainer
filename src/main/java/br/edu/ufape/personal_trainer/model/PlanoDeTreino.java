package br.edu.ufape.personal_trainer.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
public class PlanoDeTreino {
	@Id
	@GeneratedValue
	private Long planoId;
	
	@ManyToOne
	@JoinColumn(name = "alunoId")
	private Aluno aluno;
	
	@JsonIgnore
	@OneToMany(mappedBy = "plano", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemTreino> itens = new ArrayList<>();
	
	@Column(nullable = false)
	private String nome;
	
	@Column(name = "data_inicio", nullable = false)
	private LocalDate dataInicio;
	
	@Column(name = "data_fim", nullable = false)
	private LocalDate dataFim;
}
