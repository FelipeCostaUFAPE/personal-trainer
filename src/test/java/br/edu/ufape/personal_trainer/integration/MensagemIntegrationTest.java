package br.edu.ufape.personal_trainer.integration;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.dto.ChatRequest;
import br.edu.ufape.personal_trainer.dto.MensagemRequest;
import br.edu.ufape.personal_trainer.enums.Role;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Chat;
import br.edu.ufape.personal_trainer.model.Mensagem;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.PersonalRepository;
import br.edu.ufape.personal_trainer.service.ChatService;
import br.edu.ufape.personal_trainer.service.MensagemService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MensagemIntegrationTest {

    @Autowired private ChatService chatService;
    @Autowired private MensagemService mensagemService;
    @Autowired private AlunoRepository alunoRepository;
    @Autowired private PersonalRepository personalRepository;

    @Test
    @WithMockUser(username = "personal@teste.com", roles = {"PERSONAL"})
    void permiteEnvioDeMensagensComTimestampEIdentificacao() {
        Personal personal = new Personal();
        personal.setNome("Personal Teste");
        personal.setEmail("personal@teste.com");
        personal.setSenha("123");
        personal.setCref("12345-UF");
        personal.setRole(Role.PERSONAL);
        personal = personalRepository.save(personal);

        Aluno aluno = new Aluno();
        aluno.setNome("Aluno Mensagem");
        aluno.setEmail("mensagem@email.com");
        aluno.setSenha("123");
        aluno.setDataNascimento(LocalDate.of(1990, 1, 1));
        aluno.setModalidade("online");
        aluno.setObjetivo("definicao");
        aluno.setPersonal(personal);
        aluno.setRole(Role.ALUNO);
        aluno = alunoRepository.save(aluno);

        Chat chat = chatService.criar(new ChatRequest(aluno.getUsuarioId(), personal.getUsuarioId()));

        MensagemRequest reqPersonal = new MensagemRequest("Oi aluno!", false);
        Mensagem msgPersonal = mensagemService.criar(reqPersonal, chat.getChatId());
        assertFalse(msgPersonal.getEnviadoPeloAluno());
        assertNotNull(msgPersonal.getTimeStamp());

        var mensagens = mensagemService.buscarPorChatId(chat.getChatId());
        assertEquals(1, mensagens.size());
        assertNotNull(mensagens.get(0).getTimeStamp());
    }

    @Test
    @WithMockUser(username = "mensagem@email.com", roles = {"ALUNO"})
    void permiteEnvioDeMensagemPeloAluno() {
        Personal personal = new Personal();
        personal.setNome("Personal Teste");
        personal.setEmail("personal@teste.com");
        personal.setSenha("123");
        personal.setCref("12345-UF");
        personal.setRole(Role.PERSONAL);
        personal = personalRepository.save(personal);

        Aluno aluno = new Aluno();
        aluno.setNome("Aluno Mensagem");
        aluno.setEmail("mensagem@email.com");
        aluno.setSenha("123");
        aluno.setDataNascimento(LocalDate.of(1990, 1, 1));
        aluno.setModalidade("online");
        aluno.setObjetivo("definicao");
        aluno.setPersonal(personal);
        aluno.setRole(Role.ALUNO);
        aluno = alunoRepository.save(aluno);

        Chat chat = chatService.criar(new ChatRequest(aluno.getUsuarioId(), personal.getUsuarioId()));

        MensagemRequest reqAluno = new MensagemRequest("Oi personal!", true);
        Mensagem msgAluno = mensagemService.criar(reqAluno, chat.getChatId());
        assertTrue(msgAluno.getEnviadoPeloAluno());
        assertNotNull(msgAluno.getTimeStamp());

        var mensagens = mensagemService.buscarPorChatId(chat.getChatId());
        assertEquals(1, mensagens.size());
    }
}