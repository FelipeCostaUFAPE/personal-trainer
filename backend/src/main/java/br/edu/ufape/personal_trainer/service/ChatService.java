package br.edu.ufape.personal_trainer.service;

import org.springframework.beans.factory.annotation.Autowired;
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
import br.edu.ufape.personal_trainer.config.SecurityUtil;

@Service
public class ChatService {

    @Autowired private ChatRepository chatRepository;
    @Autowired private AlunoRepository alunoRepository;
    @Autowired private PersonalRepository personalRepository;

    @Transactional(readOnly = true)
    public Chat buscarId(Long id) {
        SecurityUtil.requireAuthenticated();
        return chatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe chat com ID: " + id));
    }

    @Transactional
    public Chat criar(ChatRequest request) {
        SecurityUtil.requireAuthenticated();
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
        Personal personal = personalRepository.findById(request.personalId())
                .orElseThrow(() -> new ResourceNotFoundException("Personal não encontrado"));
        if (aluno.getPersonal() == null || !aluno.getPersonal().getUsuarioId().equals(personal.getUsuarioId())) {
            throw new IllegalArgumentException("Aluno não pertence a este personal");
        }
        SecurityUtil.requireAdminPersonalOrAluno();
        if (!SecurityUtil.isAdmin()) {
            String emailLogado = SecurityUtil.getCurrentEmail();
            boolean ehAluno = aluno.getEmail().equals(emailLogado);
            boolean ehPersonal = personal.getEmail().equals(emailLogado);
            if (!ehAluno && !ehPersonal) {
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
        SecurityUtil.requireAuthenticated();
        Chat chat = buscarId(id);
        chatRepository.delete(chat);
    }

    @Transactional(readOnly = true)
    public Chat buscarPorAlunoIdAndPersonalId(Long alunoId, Long personalId) {
        SecurityUtil.requireAuthenticated();
        Chat chat = chatRepository.findByAluno_UsuarioIdAndPersonal_UsuarioId(alunoId, personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));
        SecurityUtil.requireParticipantOfChatOrAdmin(chat, "Você não participa deste chat");
        return chat;
    }
}