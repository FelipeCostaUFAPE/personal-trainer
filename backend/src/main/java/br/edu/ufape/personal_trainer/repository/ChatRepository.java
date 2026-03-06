package br.edu.ufape.personal_trainer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ufape.personal_trainer.model.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long>{

    Optional<Chat> findByAluno_UsuarioIdAndPersonal_UsuarioId(Long alunoId, Long personalId);

}