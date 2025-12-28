package br.edu.ufape.personal_trainer.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.dto.FaturaRequest;
import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.service.FaturaService;
import br.edu.ufape.personal_trainer.service.PlanoDeTreinoService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AlunoIntegrationTest {

    @Autowired private AlunoRepository alunoRepository;
    @Autowired private PlanoDeTreinoService planoService;
    @Autowired private FaturaService faturaService;

    @Test
    void alunoSemPersonalNaoPodeTerPlanoNemFatura() {
        Aluno alunoSemPersonal = new Aluno();
        alunoSemPersonal.setNome("Aluno Isolado");
        alunoSemPersonal.setEmail("isolado@email.com");
        alunoSemPersonal.setSenha("123");
        alunoSemPersonal.setDataNascimento(LocalDate.of(1990, 1, 1));
        alunoSemPersonal.setModalidade("online");
        alunoSemPersonal.setObjetivo("definicao");
        alunoSemPersonal.setPersonal(null);
        alunoSemPersonal = alunoRepository.save(alunoSemPersonal);

        PlanoDeTreinoRequest planoRequest = new PlanoDeTreinoRequest(
                alunoSemPersonal.getUsuarioId(), "Plano Isolado", LocalDate.now(), LocalDate.now().plusWeeks(4));

        assertThrows(IllegalArgumentException.class, () -> planoService.criar(planoRequest));

        FaturaRequest faturaRequest = new FaturaRequest(alunoSemPersonal.getUsuarioId(), 150.0, LocalDate.now().plusDays(30));

        assertThrows(IllegalArgumentException.class, () -> faturaService.criar(faturaRequest));
    }
}