package br.edu.ufape.personal_trainer.service;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.dto.FaturaRequest;
import br.edu.ufape.personal_trainer.enums.Role;
import br.edu.ufape.personal_trainer.enums.StatusFatura;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Fatura;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.FaturaRepository;
import br.edu.ufape.personal_trainer.repository.PersonalRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FaturaServiceTest {

    @Autowired private FaturaService faturaService;
    @Autowired private AlunoRepository alunoRepository;
    @Autowired private FaturaRepository faturaRepository;
    @Autowired private PersonalRepository personalRepository;

    @Test
    @WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
    void naoPermiteDuasFaturasPendentes() {
        Personal personal = new Personal();
        personal.setNome("Personal Teste");
        personal.setEmail("personal@teste.com");
        personal.setSenha("123");
        personal.setCref("123456-SP");
        personal.setRole(Role.PERSONAL);
        personal = personalRepository.save(personal);

        Aluno aluno = new Aluno();
        aluno.setUsuarioId(1L);
        aluno.setNome("Aluno Teste");
        aluno.setEmail("aluno@teste.com");
        aluno.setSenha("123");
        aluno.setDataNascimento(LocalDate.of(1990, 1, 1));
        aluno.setModalidade("presencial");
        aluno.setObjetivo("hipertrofia");
        aluno.setPersonal(personal);
        aluno.setRole(Role.ALUNO);
        aluno = alunoRepository.save(aluno);

        Fatura faturaExistente = new Fatura();
        faturaExistente.setAluno(aluno);
        faturaExistente.setStatus(StatusFatura.PENDENTE);
        faturaExistente.setValor(100.0);
        faturaExistente.setDataVencimento(LocalDate.now().plusDays(30));
        faturaRepository.save(faturaExistente);

        FaturaRequest request = new FaturaRequest(aluno.getUsuarioId(), 150.0, LocalDate.now().plusDays(30));

        IllegalStateException ex = assertThrows(IllegalStateException.class,() -> faturaService.criar(request));

        assertEquals("Aluno já possui fatura pendente", ex.getMessage());

        assertTrue(faturaRepository.findByAluno_UsuarioIdAndStatus(aluno.getUsuarioId(), StatusFatura.PENDENTE).isPresent());
    }

    @Test
    @WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
    void permiteCriarFaturaSemPendente() {
        Personal personal = new Personal();
        personal.setNome("Personal Teste");
        personal.setEmail("personal@teste.com");
        personal.setSenha("123");
        personal.setCref("123456-SP");
        personal.setRole(Role.PERSONAL);
        personal = personalRepository.save(personal);

        Aluno aluno = new Aluno();
        aluno.setUsuarioId(1L);
        aluno.setNome("Aluno Teste");
        aluno.setEmail("aluno@teste.com");
        aluno.setSenha("123");
        aluno.setDataNascimento(LocalDate.of(1990, 1, 1));
        aluno.setModalidade("presencial");
        aluno.setObjetivo("hipertrofia");
        aluno.setPersonal(personal);
        aluno.setRole(Role.ALUNO);
        aluno = alunoRepository.save(aluno);

        assertTrue(faturaRepository.findByAluno_UsuarioIdAndStatus(aluno.getUsuarioId(), StatusFatura.PENDENTE).isEmpty());

        FaturaRequest request = new FaturaRequest(aluno.getUsuarioId(), 200.0, LocalDate.now().plusDays(15));

        Fatura fatura = faturaService.criar(request);

        assertEquals(StatusFatura.PENDENTE, fatura.getStatus());
        assertNotNull(fatura.getAluno());
        assertEquals(200.0, fatura.getValor());
        assertEquals(LocalDate.now().plusDays(15), fatura.getDataVencimento());

        assertTrue(faturaRepository.findById(fatura.getFaturaId()).isPresent());
    }
}