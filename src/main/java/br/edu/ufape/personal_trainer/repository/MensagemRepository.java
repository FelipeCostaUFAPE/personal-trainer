package br.edu.ufape.personal_trainer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ufape.personal_trainer.model.Mensagem;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
	
    List<Mensagem> findByChat_ChatIdOrderByTimeStamp(Long chatId);
    
}