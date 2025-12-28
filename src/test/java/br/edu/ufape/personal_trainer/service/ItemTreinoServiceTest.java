package br.edu.ufape.personal_trainer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.personal_trainer.dto.ItemTreinoRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Exercicio;
import br.edu.ufape.personal_trainer.model.ItemTreino;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.repository.ExercicioRepository;
import br.edu.ufape.personal_trainer.repository.ItemTreinoRepository;
import br.edu.ufape.personal_trainer.repository.PlanoDeTreinoRepository;

@ExtendWith(MockitoExtension.class)
class ItemTreinoServiceTest {

    @Mock private ItemTreinoRepository itemRepository;
    @Mock private ExercicioRepository exercicioRepository;
    @Mock private PlanoDeTreinoRepository planoRepository;

    @InjectMocks private ItemTreinoService itemService;

    @Test
    void permiteCriarItemDeTreino() {
        Personal personal = new Personal();
        Aluno aluno = new Aluno();
        aluno.setPersonal(personal);

        PlanoDeTreino plano = new PlanoDeTreino();
        plano.setPlanoId(1L);
        plano.setAluno(aluno);

        Exercicio exercicio = new Exercicio();
        exercicio.setExercicioId(10L);

        when(planoRepository.findById(1L)).thenReturn(Optional.of(plano));
        when(exercicioRepository.findById(10L)).thenReturn(Optional.of(exercicio));
        when(itemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ItemTreinoRequest request = new ItemTreinoRequest(
                10L,
                4,
                "12",
                80.0,
                90
        );

        ItemTreino item = itemService.criar(request, 1L);

        assertNotNull(item);
        assertEquals(4, item.getSeries());
        assertEquals(exercicio, item.getExercicio());
        assertEquals(plano, item.getPlano());
        verify(itemRepository).save(any());
    }
}