package br.edu.ufape.personal_trainer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ufape.personal_trainer.enums.DiaSemana;
import br.edu.ufape.personal_trainer.model.DiaTreino;

public interface DiaTreinoRepository extends JpaRepository<DiaTreino, Long> {

    List<DiaTreino> findByPlanoPlanoId(Long planoId);

    boolean existsByPlanoPlanoIdAndDiaSemana(Long planoId, DiaSemana diaSemana);
}