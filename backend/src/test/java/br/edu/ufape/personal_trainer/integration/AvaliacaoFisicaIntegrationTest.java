package br.edu.ufape.personal_trainer.integration;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.dto.AvaliacaoFisicaRequest;
import br.edu.ufape.personal_trainer.enums.Role;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.AvaliacaoFisica;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.PersonalRepository;
import br.edu.ufape.personal_trainer.service.AvaliacaoFisicaService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AvaliacaoFisicaIntegrationTest {

    @Autowired private AvaliacaoFisicaService avaliacaoService;
    @Autowired private AlunoRepository alunoRepository;
    @Autowired private PersonalRepository personalRepository;

    @Test
    @WithMockUser(username = "personal@email.com", roles = {"PERSONAL"})
    void feitoPeloPersonalAutomaticoPorModalidade() {
        Personal personal = new Personal();
        personal.setNome("Personal Teste");
        personal.setEmail("personal@email.com");
        personal.setSenha("123");
        personal.setCref("123456-SP");
        personal = personalRepository.save(personal);

        Aluno alunoPresencial = new Aluno();
        alunoPresencial.setNome("Presencial");
        alunoPresencial.setEmail("presencial@email.com");
        alunoPresencial.setSenha("123");
        alunoPresencial.setDataNascimento(LocalDate.of(1990, 1, 1));
        alunoPresencial.setModalidade("presencial");
        alunoPresencial.setObjetivo("hipertrofia");
        alunoPresencial.setPersonal(personal);
        alunoPresencial.setRole(Role.ALUNO);
        alunoPresencial = alunoRepository.save(alunoPresencial);

        Aluno alunoOnline = new Aluno();
        alunoOnline.setNome("Online");
        alunoOnline.setEmail("online@email.com");
        alunoOnline.setSenha("123");
        alunoOnline.setDataNascimento(LocalDate.of(1990, 1, 1));
        alunoOnline.setModalidade("online");
        alunoOnline.setObjetivo("emagrecimento");
        alunoOnline.setPersonal(personal);
        alunoOnline.setRole(Role.ALUNO);
        
        Aluno alunoOnlineSalvo = alunoRepository.save(alunoOnline);

        AvaliacaoFisicaRequest requestPresencial = new AvaliacaoFisicaRequest(
                alunoPresencial.getUsuarioId(),
                LocalDate.now(),
                80.0,
                1.75,
                15.0,
                "obs",
                true
        );
        AvaliacaoFisica avPresencial = avaliacaoService.criar(requestPresencial, alunoPresencial);
        assertTrue(avPresencial.getFeitoPeloPersonal());

        AvaliacaoFisicaRequest requestOnline = new AvaliacaoFisicaRequest(
                alunoOnlineSalvo.getUsuarioId(),
                LocalDate.now(),
                70.0,
                1.70,
                12.0,
                "obs",
                false
        );

        assertThrows(IllegalArgumentException.class,
                () -> avaliacaoService.criar(requestOnline, alunoOnlineSalvo),
                "Personal não deve poder criar avaliação para aluno online");
    }
}