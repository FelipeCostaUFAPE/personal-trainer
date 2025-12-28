package br.edu.ufape.personal_trainer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.personal_trainer.dto.FaturaRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Fatura;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.FaturaRepository;

@ExtendWith(MockitoExtension.class)
class FaturaServiceTest {

    @Mock private FaturaRepository faturaRepository;
    @Mock private AlunoRepository alunoRepository;

    @InjectMocks private FaturaService faturaService;

    private Aluno aluno;

    @BeforeEach
    void setup() {
        Personal personal = new Personal();
        aluno = new Aluno();
        aluno.setUsuarioId(1L);
        aluno.setPersonal(personal);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
    }

    @Test
    void naoPermiteDuasFaturasPendentes() {
        when(faturaRepository.findByAluno_UsuarioIdAndStatus(1L, "PENDENTE"))
                .thenReturn(Optional.of(new Fatura()));

        FaturaRequest request = new FaturaRequest(1L, 150.0, LocalDate.now().plusDays(30));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> faturaService.criar(request));

        assertEquals("Aluno já possui uma fatura pendente", ex.getMessage());
        verify(faturaRepository, never()).save(any());
    }

    @Test
    void permiteCriarFaturaSemPendente() {
        when(faturaRepository.findByAluno_UsuarioIdAndStatus(1L, "PENDENTE"))
                .thenReturn(Optional.empty());
        when(faturaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        FaturaRequest request = new FaturaRequest(1L, 200.0, LocalDate.now().plusDays(15));
        Fatura fatura = faturaService.criar(request);

        assertEquals("PENDENTE", fatura.getStatus());
        verify(faturaRepository).save(any());
    }
}