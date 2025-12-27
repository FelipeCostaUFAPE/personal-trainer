package br.edu.ufape.personal_trainer.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.dto.MensagemRequest;
import br.edu.ufape.personal_trainer.model.Chat;
import br.edu.ufape.personal_trainer.model.Mensagem;
import br.edu.ufape.personal_trainer.repository.ChatRepository;
import br.edu.ufape.personal_trainer.repository.MensagemRepository;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository mensagemRepository;
    
    @Autowired
    private ChatRepository chatRepository;

    // listar todos
    @Transactional(readOnly = true)
    public List<Mensagem> listarTodos() {
        return mensagemRepository.findAll();
    }

    // buscar id
    @Transactional(readOnly = true)
    public Mensagem buscarId(Long id) {
        return mensagemRepository.findById(id).orElseThrow(() -> new RuntimeException("Não existe mensagem com ID: " + id));
    }
    
    // criar dto
    @Transactional
    public Mensagem criar(MensagemRequest request, Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat não encontrado"));

        Mensagem mensagem = new Mensagem();
        mensagem.setConteudo(request.conteudo());
        mensagem.setEnviadoPeloAluno(request.enviadoPeloAluno());
        mensagem.setTimeStamp(LocalDateTime.now());
        mensagem.setChat(chat);
        return mensagemRepository.save(mensagem);
    }

    // salvar (MUITO PROVAVELMENTE ADICIONAR MAIS)
    @Transactional
    public Mensagem salvar(Mensagem mensagem) {
        if (mensagem.getChat() == null) {
            throw new IllegalArgumentException("Mensagem deve pertencer a um chat");
        }
        return mensagemRepository.save(mensagem);
    }

    // deletar
    @Transactional
    public void deletar(Long id) {
        if (!mensagemRepository.existsById(id)) {
            throw new RuntimeException("Não existe mensagem com ID: " + id);
        }
        mensagemRepository.deleteById(id);
    }

    // métodos personalizados
    @Transactional(readOnly = true)
    public List<Mensagem> buscarPorChatId(Long chatId) {
        return mensagemRepository.findByChat_ChatIdOrderByTimeStamp(chatId);
    }

}