package br.edu.ufape.personal_trainer.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class Exercicio {
	@Id
	@GeneratedValue
	private Long exercicioId;
	
	@ManyToOne
	@JoinColumn(name = "grupoMuscularId")
	private GrupoMuscular grupoMuscular;
	
	@JsonIgnore
	@OneToMany(mappedBy = "exercicio")
	private List<ItemTreino> itens;
	
	@Column(unique = true, nullable = false)
	private String nome;
	
	@Column(nullable = false)
	private String descricao;
	
}
