package br.edu.ufape.personal_trainer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import br.edu.ufape.personal_trainer.enums.DiaSemana;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
public class DiaTreino {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiaSemana diaSemana;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "plano_id", nullable = false)
    private PlanoDeTreino plano;

    @JsonManagedReference
    @OneToMany(mappedBy = "diaTreino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemTreino> itens = new ArrayList<>();
}