package br.edu.ufape.personal_trainer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

import br.edu.ufape.personal_trainer.controller.advice.BusinessValidationException;
import br.edu.ufape.personal_trainer.model.GrupoMuscular;
import br.edu.ufape.personal_trainer.repository.GrupoMuscularRepository;

@ExtendWith(MockitoExtension.class)
class GrupoMuscularServiceTest {

    @Mock private GrupoMuscularRepository grupoRepository;
    @InjectMocks private GrupoMuscularService grupoService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void naoPermiteGrupoComNomeDuplicado() {
        Authentication auth = mock(Authentication.class);
        lenient().when(auth.isAuthenticated()).thenReturn(true);
        lenient().when(auth.getAuthorities()).thenAnswer(invocation ->
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(grupoRepository.findByNome("Peito")).thenReturn(Optional.of(new GrupoMuscular()));

        GrupoMuscular grupo = new GrupoMuscular();
        grupo.setNome("Peito");

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> grupoService.criar(grupo));

        assertTrue(ex.getErrors().containsKey("nome"));
        assertEquals("Nome do grupo muscular já existe", ex.getErrors().get("nome"));
        verify(grupoRepository, never()).save(any());
    }

    @Test
    void permiteCriarGrupoComNomeNovo() {
        Authentication auth = mock(Authentication.class);
        lenient().when(auth.isAuthenticated()).thenReturn(true);
        lenient().when(auth.getAuthorities()).thenAnswer(invocation ->
            List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(grupoRepository.findByNome("Trapézio")).thenReturn(Optional.empty());
        when(grupoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        GrupoMuscular grupo = new GrupoMuscular();
        grupo.setNome("Trapézio");

        GrupoMuscular salvo = grupoService.criar(grupo);

        assertNotNull(salvo);
        assertEquals("Trapézio", salvo.getNome());
        verify(grupoRepository).save(any());
    }
}