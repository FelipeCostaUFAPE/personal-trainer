package br.edu.ufape.personal_trainer.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.ChatRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Chat;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.ChatRepository;
import br.edu.ufape.personal_trainer.repository.PersonalRepository;

@Service
public class ChatService {

    @Autowired 
    private ChatRepository chatRepository;
    
    @Autowired 
    private AlunoRepository alunoRepository;
    
    @Autowired 
    private PersonalRepository personalRepository;

    @Transactional(readOnly = true)
    public List<Chat> listarTodos() {
        return chatRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Chat buscarId(Long id) {
        return chatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe chat com ID: " + id));
    }

    @Transactional
    public Chat criar(ChatRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        String usuarioLogado = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
        
        Personal personal = personalRepository.findById(request.personalId())
                .orElseThrow(() -> new ResourceNotFoundException("Personal não encontrado"));

        if (aluno.getPersonal() == null || !aluno.getPersonal().getUsuarioId().equals(personal.getUsuarioId())) {
            throw new IllegalArgumentException("Aluno não pertence a este personal");
        }

        if (!isAdmin) {
            boolean usuarioEhAluno = aluno.getEmail().equals(usuarioLogado);
            boolean usuarioEhPersonal = personal.getEmail().equals(usuarioLogado);
            if (!usuarioEhAluno && !usuarioEhPersonal) {
                throw new IllegalArgumentException("Usuário não pode criar chat entre terceiros");
            }
        }

        if (chatRepository.findByAluno_UsuarioIdAndPersonal_UsuarioId(request.alunoId(), request.personalId()).isPresent()) {
            throw new IllegalStateException("Chat já existe");
        }

        Chat chat = new Chat();
        chat.setAluno(aluno);
        chat.setPersonal(personal);
        return chatRepository.save(chat);
    }

    @Transactional
    public void deletar(Long id) {
        Chat chat = buscarId(id);
        chatRepository.delete(chat);
    }

    @Transactional(readOnly = true)
    public Chat buscarPorAlunoIdAndPersonalId(Long alunoId, Long personalId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        String usuarioLogado = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Chat chat = chatRepository.findByAluno_UsuarioIdAndPersonal_UsuarioId(alunoId, personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));

        if (!isAdmin) {
            boolean ehAluno = chat.getAluno().getEmail().equals(usuarioLogado);
            boolean ehPersonal = chat.getPersonal().getEmail().equals(usuarioLogado);
            if (!ehAluno && !ehPersonal) {
                throw new AccessDeniedException("Você não participa deste chat");
            }
        }

        return chat;
    }
}