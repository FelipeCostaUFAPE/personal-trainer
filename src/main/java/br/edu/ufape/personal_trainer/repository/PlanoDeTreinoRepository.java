package br.edu.ufape.personal_trainer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ufape.personal_trainer.model.PlanoDeTreino;

@Repository
public interface PlanoDeTreinoRepository extends JpaRepository<PlanoDeTreino, Long>{
	
	List<PlanoDeTreino> findByAluno_UsuarioId(Long alunoId);
	
	List<PlanoDeTreino> findByNome(String nome);

	List<PlanoDeTreino> findByAlunoUsuarioId(Long alunoId);
}