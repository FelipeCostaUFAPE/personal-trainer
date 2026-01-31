package br.edu.ufape.personal_trainer.integration;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.dto.ItemTreinoRequest;
import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoRequest;
import br.edu.ufape.personal_trainer.enums.DiaSemana;
import br.edu.ufape.personal_trainer.model.*;
import br.edu.ufape.personal_trainer.repository.*;
import br.edu.ufape.personal_trainer.service.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PlanoItemIntegrationTest {

    @Autowired private PlanoDeTreinoService planoService;
    @Autowired private ItemTreinoService itemService;
    @Autowired private AlunoRepository alunoRepository;
    @Autowired private PersonalRepository personalRepository;
    @Autowired private ExercicioRepository exercicioRepository;
    @Autowired private DiaTreinoService diaTreinoService;

    @Test
    void permiteItensDuplicadosNoMesmoPlano() {
        Personal personal = new Personal();
        personal.setNome("Personal Teste");
        personal.setEmail("personal@teste.com");
        personal.setSenha("123");
        personal.setCref("123456-SP");
        personal = personalRepository.save(personal);

        Aluno aluno = new Aluno();
        aluno.setNome("Aluno Duplicado");
        aluno.setEmail("aluno@duplicado.com");
        aluno.setSenha("123");
        aluno.setDataNascimento(LocalDate.of(1990, 1, 1));
        aluno.setModalidade("presencial");
        aluno.setObjetivo("hipertrofia");
        aluno.setPersonal(personal);
        aluno = alunoRepository.save(aluno);

        Exercicio exercicio = new Exercicio();
        exercicio.setNome("Supino Reto");
        exercicio.setDescricao("Peito");
        exercicio = exercicioRepository.save(exercicio);

        PlanoDeTreinoRequest planoRequest = new PlanoDeTreinoRequest(
            aluno.getUsuarioId(), "Plano A/B", LocalDate.now(), LocalDate.now().plusWeeks(12)
        );
        PlanoDeTreino plano = planoService.criar(planoRequest);

        DiaTreino dia = new DiaTreino();
        dia.setDiaSemana(DiaSemana.SEGUNDA);
        dia.setPlano(plano);
        dia = diaTreinoService.adicionarDia(plano.getPlanoId(), dia);

        ItemTreinoRequest itemReq1 = new ItemTreinoRequest(
            exercicio.getExercicioId(), 4, "10-12", 80.0, 120
        );
        itemService.criar(itemReq1, dia.getId());

        ItemTreinoRequest itemReq2 = new ItemTreinoRequest(
            exercicio.getExercicioId(), 3, "8-10", 90.0, 90
        );
        itemService.criar(itemReq2, dia.getId());

        DiaTreino diaAtualizado = diaTreinoService.buscarPorId(dia.getId());
        assertEquals(2, diaAtualizado.getItens().size());
    }
}