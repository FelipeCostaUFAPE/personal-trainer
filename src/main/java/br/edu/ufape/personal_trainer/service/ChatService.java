package br.edu.ufape.personal_trainer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.controller.advice.BusinessValidationException;
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
        Map<String, String> errors = new HashMap<>();

        if (chatRepository.findByAluno_UsuarioIdAndPersonal_UsuarioId(request.alunoId(), request.personalId()).isPresent()) {
            errors.put("chat", "Chat já existe entre este aluno e personal");
        }

        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        Personal personal = personalRepository.findById(request.personalId())
                .orElseThrow(() -> new ResourceNotFoundException("Personal não encontrado"));

        if (aluno.getPersonal() == null || !aluno.getPersonal().getUsuarioId().equals(personal.getUsuarioId())) {
            errors.put("personal", "Aluno não está vinculado a este personal");
        }

        if (!errors.isEmpty()) {
            throw new BusinessValidationException(errors);
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
    public List<Chat> buscarPorAluno(Long alunoId) {
        return chatRepository.findByAluno_UsuarioId(alunoId);
    }

    @Transactional(readOnly = true)
    public List<Chat> buscarPorPersonal(Long personalId) {
        return chatRepository.findByPersonal_UsuarioId(personalId);
    }

    @Transactional(readOnly = true)
    public Chat buscarPorAlunoIdAndPersonalId(Long alunoId, Long personalId) {
        return chatRepository.findByAluno_UsuarioIdAndPersonal_UsuarioId(alunoId, personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado entre aluno ID " + alunoId + " e personal ID " + personalId));
    }
}