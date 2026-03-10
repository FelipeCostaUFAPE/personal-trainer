package br.edu.ufape.personal_trainer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import br.edu.ufape.personal_trainer.dto.ExercicioRequest;
import br.edu.ufape.personal_trainer.model.Exercicio;
import br.edu.ufape.personal_trainer.model.GrupoMuscular;
import br.edu.ufape.personal_trainer.repository.ExercicioRepository;
import br.edu.ufape.personal_trainer.repository.GrupoMuscularRepository;

@ExtendWith(MockitoExtension.class)
class ExercicioServiceTest {

    @Mock private ExercicioRepository exercicioRepository;
    @Mock private GrupoMuscularRepository grupoRepository;
    @InjectMocks private ExercicioService exercicioService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void permiteCriarExercicioComNomeNovo() {
        Authentication auth = mock(Authentication.class);
        lenient().when(auth.isAuthenticated()).thenReturn(true);
        lenient().when(auth.getAuthorities()).thenAnswer(invocation ->
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        GrupoMuscular grupo = new GrupoMuscular();
        grupo.setGrupoMuscularId(1L);
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo));
        when(exercicioRepository.findByNomeContainingIgnoreCase("Remada Curvada"))
                .thenReturn(Collections.emptyList());
        when(exercicioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ExercicioRequest request = new ExercicioRequest(
                1L,
                "Remada Curvada",
                "Exercício para costas"
        );

        Exercicio exercicio = exercicioService.criar(request);

        assertNotNull(exercicio);
        assertEquals("Remada Curvada", exercicio.getNome());
        assertEquals(grupo, exercicio.getGrupoMuscular());
        verify(exercicioRepository).save(any());
    }
}