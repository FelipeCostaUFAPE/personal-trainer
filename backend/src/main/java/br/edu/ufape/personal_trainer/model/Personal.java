package br.edu.ufape.personal_trainer.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Personal extends Usuario {

    @Column(unique = true, nullable = false)
    private String cref;

    @JsonIgnore
    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chats = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Aluno> alunos = new ArrayList<>();
}