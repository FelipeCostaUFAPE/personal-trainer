package br.edu.ufape.personal_trainer.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Aluno extends Usuario{
	@ManyToOne
	@JoinColumn(name = "personalId")
	private Personal personal;
	
	@JsonIgnore
	@OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<AvaliacaoFisica> avaliacoes;
	
	@JsonIgnore
	@OneToOne(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
	private Chat chat;
	
	@OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Fatura> faturas = new ArrayList<>();
	
	@JsonIgnore
	@OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PlanoDeTreino> planos = new ArrayList<>();
	
	@Column(nullable = false)
	private String modalidade;
	
	@Column(name = "data_nascimento", nullable = false)
	private LocalDate dataNascimento;
	
	@Column(nullable = false)
	private Boolean ativo = false;
	
	@Column(nullable = false)
	private String objetivo;
}