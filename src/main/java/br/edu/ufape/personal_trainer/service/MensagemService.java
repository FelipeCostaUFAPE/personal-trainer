package br.edu.ufape.personal_trainer.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
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

    @Transactional(readOnly = true)
    public List<Mensagem> listarTodos() {
        return mensagemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Mensagem buscarId(Long id) {
        return mensagemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe mensagem com ID: " + id));
    }

    @Transactional
    public Mensagem criar(MensagemRequest request, Long chatId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));

        if (!isAdmin) {
            boolean ehAlunoDoChat = chat.getAluno().getEmail().equals(usuarioLogado);
            boolean ehPersonalDoChat = chat.getPersonal().getEmail().equals(usuarioLogado);
            if (!ehAlunoDoChat && !ehPersonalDoChat) {
                throw new IllegalArgumentException("Usuário não pertence a este chat");
            }
            if (request.enviadoPeloAluno() && !ehAlunoDoChat) {
                throw new IllegalArgumentException("Mensagem inválida: usuário não é o aluno");
            }
            if (!request.enviadoPeloAluno() && !ehPersonalDoChat) {
                throw new IllegalArgumentException("Mensagem inválida: usuário não é o personal");
            }
        }

        Mensagem mensagem = new Mensagem();
        mensagem.setConteudo(request.conteudo());
        mensagem.setEnviadoPeloAluno(request.enviadoPeloAluno());
        mensagem.setTimeStamp(LocalDateTime.now());
        mensagem.setChat(chat);

        return mensagemRepository.save(mensagem);
    }

    @Transactional
    public void deletar(Long id) {
        if (!mensagemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Não existe mensagem com ID: " + id);
        }
        mensagemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Mensagem> buscarPorChatId(Long chatId) {
        return mensagemRepository.findByChat_ChatIdOrderByTimeStamp(chatId);
    }
}