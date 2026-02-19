package br.edu.ufape.personal_trainer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoRequest;
import br.edu.ufape.personal_trainer.enums.Role;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.model.Usuario;
import br.edu.ufape.personal_trainer.repository.PlanoDeTreinoRepository;

@ExtendWith(MockitoExtension.class)
class PlanoDeTreinoServiceTest {

    @Mock private PlanoDeTreinoRepository planoRepository;
    @Mock private AuthService authService;
    @Mock private AlunoService alunoService;

    @InjectMocks private PlanoDeTreinoService planoService;

    private Aluno aluno;

    @BeforeEach
    void setup() {
        Personal personal = new Personal();
        personal.setUsuarioId(10L);
        personal.setRole(Role.PERSONAL);

        aluno = new Aluno();
        aluno.setUsuarioId(1L);
        aluno.setPersonal(personal);
        aluno.setRole(Role.ALUNO);
    }

    @Test
    void naoPermiteCriarPlanoParaAlunoSemPersonal() {
        Aluno alunoSemPersonal = new Aluno();
        alunoSemPersonal.setUsuarioId(2L);
        alunoSemPersonal.setPersonal(null);
        alunoSemPersonal.setRole(Role.ALUNO);

        when(alunoService.buscarId(2L)).thenReturn(alunoSemPersonal);

        PlanoDeTreinoRequest request = new PlanoDeTreinoRequest(
                2L,
                "Plano Teste",
                LocalDate.now(),
                LocalDate.now().plusWeeks(4)
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> planoService.criar(request)
        );

        assertEquals("Aluno precisa estar vinculado a um personal", ex.getMessage());

        verify(planoRepository, never()).save(any());
        verify(authService, never()).usuarioLogado();
    }

    @Test
    void permiteCriarPlanoParaAlunoComPersonal() {

        Usuario usuarioLogado = new Personal();
        usuarioLogado.setUsuarioId(10L);
        usuarioLogado.setRole(Role.PERSONAL);

        when(authService.usuarioLogado()).thenReturn(usuarioLogado);
        when(alunoService.buscarId(1L)).thenReturn(aluno);
        when(planoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PlanoDeTreinoRequest request = new PlanoDeTreinoRequest(
                1L,
                "Plano Semanal",
                LocalDate.now(),
                LocalDate.now().plusWeeks(8)
        );

        PlanoDeTreino plano = planoService.criar(request);

        assertNotNull(plano);
        assertEquals("Plano Semanal", plano.getNome());
        assertEquals(aluno, plano.getAluno());

        verify(planoRepository).save(any());
        verify(authService).usuarioLogado();
    }
}
