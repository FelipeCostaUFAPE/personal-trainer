package br.edu.ufape.personal_trainer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ufape.personal_trainer.model.ItemTreino;

@Repository
public interface ItemTreinoRepository extends JpaRepository<ItemTreino, Long>{
	
	List<ItemTreino> findByExercicio_ExercicioId(Long exercicioId);
}
