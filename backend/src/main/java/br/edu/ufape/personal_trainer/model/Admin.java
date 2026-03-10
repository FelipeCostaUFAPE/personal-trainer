package br.edu.ufape.personal_trainer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Admin extends Usuario {}