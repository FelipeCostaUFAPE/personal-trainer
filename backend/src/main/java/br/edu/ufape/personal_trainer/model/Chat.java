package br.edu.ufape.personal_trainer.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
public class Chat {
	@Id
	@GeneratedValue
	private Long chatId;
	
	@ManyToOne
	@JoinColumn(name = "personalId")
	private Personal personal;
	
	@ManyToOne
	@JoinColumn(name = "alunoId")
	private Aluno aluno;
	
	@JsonIgnore
	@OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Mensagem> mensagens = new ArrayList<>();
}
