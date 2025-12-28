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
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Exercicio;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.ExercicioRepository;
import br.edu.ufape.personal_trainer.repository.PersonalRepository;
import br.edu.ufape.personal_trainer.service.ItemTreinoService;
import br.edu.ufape.personal_trainer.service.PlanoDeTreinoService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PlanoItemIntegrationTest {

    @Autowired private PlanoDeTreinoService planoService;
    @Autowired private ItemTreinoService itemService;
    @Autowired private AlunoRepository alunoRepository;
    @Autowired private PersonalRepository personalRepository;
    @Autowired private ExercicioRepository exercicioRepository;

    @Test
    void permiteItensDuplicadosNoMesmoPlano() {
        Personal personal = personalRepository.save(new Personal());

        Aluno aluno = new Aluno();
        aluno.setNome("Aluno Duplicado");
        aluno.setEmail("duplicado@email.com");
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

        PlanoDeTreinoRequest planoRequest = new PlanoDeTreinoRequest(aluno.getUsuarioId(), "Plano A/B", LocalDate.now(), LocalDate.now().plusWeeks(12));
        PlanoDeTreino plano = planoService.criar(planoRequest);

        itemService.criar(new ItemTreinoRequest(exercicio.getExercicioId(), 4, "10-12", 80.0, 120), plano.getPlanoId());
        itemService.criar(new ItemTreinoRequest(exercicio.getExercicioId(), 3, "8-10", 90.0, 90), plano.getPlanoId());

        PlanoDeTreino planoAtualizado = planoService.buscarId(plano.getPlanoId());

        assertEquals(2, planoAtualizado.getItens().size());
    }
}