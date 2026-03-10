package br.edu.ufape.personal_trainer.integration;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.dto.FaturaRequest;
import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoRequest;
import br.edu.ufape.personal_trainer.enums.Role;
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
    @WithMockUser(username = "admin@email.com", roles = {"ADMIN"})
    void deveLancarExcecaoAoTentarCriarPlanoOuFaturaParaAlunoSemPersonalVinculado() {
        Aluno aluno = new Aluno();
        aluno.setNome("Aluno Isolado");
        aluno.setEmail("isolado@email.com");
        aluno.setSenha("123");
        aluno.setDataNascimento(LocalDate.of(1990, 1, 1));
        aluno.setModalidade("online");
        aluno.setObjetivo("definicao");
        aluno.setPersonal(null);
        aluno.setRole(Role.ALUNO);

        aluno = alunoRepository.saveAndFlush(aluno);

        PlanoDeTreinoRequest planoRequest = new PlanoDeTreinoRequest(
                aluno.getUsuarioId(),
                "Plano Isolado",
                LocalDate.now(),
                LocalDate.now().plusWeeks(4)
        );

        assertThrows(IllegalArgumentException.class,
                () -> planoService.criar(planoRequest),
                "Deveria lançar exceção ao criar plano para aluno sem personal");

        FaturaRequest faturaRequest = new FaturaRequest(
                aluno.getUsuarioId(),
                150.0,
                LocalDate.now().plusDays(30)
        );

        assertThrows(IllegalArgumentException.class,
                () -> faturaService.criar(faturaRequest),
                "Deveria lançar exceção ao criar fatura para aluno sem personal");
    }
}