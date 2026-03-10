package br.edu.ufape.personal_trainer.repository;

import br.edu.ufape.personal_trainer.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
    Optional<Usuario> findByEmail(String email);
}