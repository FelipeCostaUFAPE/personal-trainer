package br.edu.ufape.personal_trainer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ufape.personal_trainer.model.GrupoMuscular;

@Repository
public interface GrupoMuscularRepository extends JpaRepository<GrupoMuscular, Long>{
	
	Optional<GrupoMuscular> findByNome(String nome);
}
