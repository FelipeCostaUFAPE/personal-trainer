package br.edu.ufape.personal_trainer.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
public class GrupoMuscular {
	@Id
	@GeneratedValue
	private Long grupoMuscularId;
	
	@JsonIgnore
	@OneToMany(mappedBy = "grupoMuscular")
	private List<Exercicio> exercicios = new ArrayList<>();
	
	@Column(unique = true, nullable = false)
	private String nome;
	
	@Column(nullable = false)
	private String descricao;
}
