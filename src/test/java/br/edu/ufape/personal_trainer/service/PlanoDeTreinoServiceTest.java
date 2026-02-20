package br.edu.ufape.personal_trainer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoRequest;
import br.edu.ufape.personal_trainer.enums.Role;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.repository.PlanoDeTreinoRepository;

@ExtendWith(MockitoExtension.class)
class PlanoDeTreinoServiceTest {

    @Mock private PlanoDeTreinoRepository planoRepository;
    @Mock private AlunoService alunoService;

    @InjectMocks private PlanoDeTreinoService planoService;

    private Aluno aluno;

    @BeforeEach
    void setup() {
        Personal personal = new Personal();
        personal.setUsuarioId(10L);
        personal.setEmail("personal@email.com");
        personal.setRole(Role.PERSONAL);
        aluno = new Aluno();
        aluno.setUsuarioId(1L);
        aluno.setPersonal(personal);
        aluno.setRole(Role.ALUNO);
    }

    @Test
    void naoPermiteCriarPlanoParaAlunoSemPersonal() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("personal@email.com");
        when(auth.isAuthenticated()).thenReturn(true);
        doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_PERSONAL")))
            .when(auth).getAuthorities();

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

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

        SecurityContextHolder.clearContext();
    }

    @Test
    void permiteCriarPlanoParaAlunoComPersonal() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("personal@email.com");
        when(auth.isAuthenticated()).thenReturn(true);
        doReturn(Collections.singleton(new SimpleGrantedAuthority("ROLE_PERSONAL")))
            .when(auth).getAuthorities();

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

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

        SecurityContextHolder.clearContext();
    }
}