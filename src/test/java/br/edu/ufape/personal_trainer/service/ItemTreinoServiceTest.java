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
import br.edu.ufape.personal_trainer.enums.Role;
import br.edu.ufape.personal_trainer.model.*;
import br.edu.ufape.personal_trainer.repository.*;

@ExtendWith(MockitoExtension.class)
class ItemTreinoServiceTest {

    @Mock private ItemTreinoRepository itemRepository;
    @Mock private ExercicioRepository exercicioRepository;
    @Mock private DiaTreinoRepository diaTreinoRepository;
    @Mock private PlanoDeTreinoRepository planoRepository;
    @Mock private AuthService authService;

    @InjectMocks private ItemTreinoService itemService;

    @Test
    void permiteCriarItemDeTreino() {
        Personal usuarioLogado = new Personal();
        usuarioLogado.setUsuarioId(100L);
        usuarioLogado.setRole(Role.PERSONAL);
        when(authService.usuarioLogado()).thenReturn(usuarioLogado);

        DiaTreino dia = new DiaTreino();
        dia.setId(1L);
        PlanoDeTreino plano = new PlanoDeTreino();
        Aluno aluno = new Aluno();
        Personal personal = new Personal();
        personal.setUsuarioId(100L);
        personal.setRole(Role.PERSONAL);
        aluno.setPersonal(personal);
        plano.setAluno(aluno);
        dia.setPlano(plano);

        Exercicio exercicio = new Exercicio();
        exercicio.setExercicioId(10L);

        when(diaTreinoRepository.findById(1L)).thenReturn(Optional.of(dia));
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
        assertEquals(dia, item.getDiaTreino());

        verify(itemRepository).save(any());
    }
}