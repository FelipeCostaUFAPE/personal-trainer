package br.edu.ufape.personal_trainer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ufape.personal_trainer.model.Personal;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, Long>{
	
	Optional<Personal> findByCref(String cref);
	
	Optional<Personal> findByEmail(String email);
	
}
