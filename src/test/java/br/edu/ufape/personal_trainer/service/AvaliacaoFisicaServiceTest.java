package br.edu.ufape.personal_trainer.service;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.dto.AvaliacaoFisicaRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.AvaliacaoFisica;
import br.edu.ufape.personal_trainer.model.Personal;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AvaliacaoFisicaServiceTest {

    @Autowired private AvaliacaoFisicaService avaliacaoService;

    @Test
    @WithMockUser(username = "personal@teste.com", roles = {"PERSONAL"})
    void feitoPeloPersonalTrueQuandoModalidadePresencial() {
        Personal personal = new Personal();
        personal.setEmail("personal@teste.com");

        Aluno aluno = new Aluno();
        aluno.setPersonal(personal);
        aluno.setModalidade("presencial");

        AvaliacaoFisicaRequest request = new AvaliacaoFisicaRequest(
                1L, LocalDate.now(), 80.0, 1.75, 15.0, "obs", true);

        AvaliacaoFisica av = avaliacaoService.criar(request, aluno);
        assertTrue(av.getFeitoPeloPersonal());
    }

    @Test
    @WithMockUser(username = "personal@teste.com", roles = {"PERSONAL"})
    void feitoPeloPersonalFalseQuandoModalidadeOnline() {
        Personal personal = new Personal();
        personal.setEmail("personal@teste.com");

        Aluno aluno = new Aluno();
        aluno.setPersonal(personal);
        aluno.setModalidade("online");

        AvaliacaoFisicaRequest request = new AvaliacaoFisicaRequest(
                1L, LocalDate.now(), 70.0, 1.70, 12.0, "obs", false);

        assertThrows(IllegalArgumentException.class,
                () -> avaliacaoService.criar(request, aluno),
                "Personal não deve poder criar avaliação para aluno online");
    }
}