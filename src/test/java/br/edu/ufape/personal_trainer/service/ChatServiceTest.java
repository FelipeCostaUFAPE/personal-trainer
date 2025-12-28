package br.edu.ufape.personal_trainer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.personal_trainer.controller.advice.BusinessValidationException;
import br.edu.ufape.personal_trainer.dto.ChatRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.ChatRepository;
import br.edu.ufape.personal_trainer.repository.PersonalRepository;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private ChatRepository chatRepository;
    @Mock private AlunoRepository alunoRepository;
    @Mock private PersonalRepository personalRepository;

    @InjectMocks private ChatService chatService;

    @Test
    void naoPermiteChatComPersonalDiferenteDoVinculado() {
        Personal personalVinculado = new Personal();
        personalVinculado.setUsuarioId(10L);

        Aluno aluno = new Aluno();
        aluno.setUsuarioId(1L);
        aluno.setPersonal(personalVinculado);

        Personal personalErrado = new Personal();
        personalErrado.setUsuarioId(99L);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(personalRepository.findById(99L)).thenReturn(Optional.of(personalErrado));

        ChatRequest request = new ChatRequest(1L, 99L);

        var ex = assertThrows(BusinessValidationException.class,
                () -> chatService.criar(request));

        assertTrue(ex.getErrors().containsKey("personal"));
        assertEquals("Aluno não está vinculado a este personal", ex.getErrors().get("personal"));
    }
}