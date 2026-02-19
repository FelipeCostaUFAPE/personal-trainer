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
import br.edu.ufape.personal_trainer.dto.MensagemRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Chat;
import br.edu.ufape.personal_trainer.model.Mensagem;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.ChatRepository;
import br.edu.ufape.personal_trainer.repository.MensagemRepository;

@ExtendWith(MockitoExtension.class)
class MensagemServiceTest {

    @Mock private MensagemRepository mensagemRepository;
    @Mock private ChatRepository chatRepository;

    @InjectMocks private MensagemService mensagemService;

    @Test
    void permiteCriarMensagem() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("aluno@email.com");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        Chat chat = new Chat();
        chat.setChatId(1L);
        Aluno aluno = new Aluno();
        aluno.setEmail("aluno@email.com");
        Personal personal = new Personal();
        personal.setEmail("personal@email.com");
        chat.setAluno(aluno);
        chat.setPersonal(personal);

        when(chatRepository.findById(1L)).thenReturn(Optional.of(chat));
        when(mensagemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        MensagemRequest request = new MensagemRequest("Olá, tudo bem?", true);

        Mensagem mensagem = mensagemService.criar(request, 1L);

        assertNotNull(mensagem);
        assertEquals("Olá, tudo bem?", mensagem.getConteudo());
        assertTrue(mensagem.getEnviadoPeloAluno());
        assertEquals(chat, mensagem.getChat());
        assertNotNull(mensagem.getTimeStamp());

        verify(mensagemRepository).save(any());

        SecurityContextHolder.clearContext();
    }
}