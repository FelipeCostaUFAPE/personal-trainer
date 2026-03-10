package br.edu.ufape.personal_trainer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ufape.personal_trainer.model.Exercicio;

@Repository
public interface ExercicioRepository extends JpaRepository<Exercicio, Long>{
	
	List<Exercicio> findByGrupoMuscular_GrupoMuscularId(Long grupoMuscularId);
	
	List<Exercicio> findByNomeContainingIgnoreCase(String nome);
}
