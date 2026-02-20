package br.edu.ufape.personal_trainer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("aluno@email.com");
        when(auth.isAuthenticated()).thenReturn(true);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        Personal personalVinculado = new Personal();
        personalVinculado.setUsuarioId(10L);
        personalVinculado.setEmail("personal@vinculado.com");

        Aluno aluno = new Aluno();
        aluno.setUsuarioId(1L);
        aluno.setEmail("aluno@email.com");
        aluno.setPersonal(personalVinculado);

        Personal personalErrado = new Personal();
        personalErrado.setUsuarioId(99L);
        personalErrado.setEmail("personal@errado.com");

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(personalRepository.findById(99L)).thenReturn(Optional.of(personalErrado));

        ChatRequest request = new ChatRequest(1L, 99L);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> chatService.criar(request));

        assertEquals("Aluno não pertence a este personal", ex.getMessage());

        verify(chatRepository, never()).save(any());

        SecurityContextHolder.clearContext();
    }
}