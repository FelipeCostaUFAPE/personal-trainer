package br.edu.ufape.personal_trainer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.personal_trainer.dto.AvaliacaoFisicaRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.AvaliacaoFisica;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.AvaliacaoFisicaRepository;

@ExtendWith(MockitoExtension.class)
class AvaliacaoFisicaServiceTest {

    @Mock private AvaliacaoFisicaRepository avaliacaoRepository;

    @InjectMocks private AvaliacaoFisicaService avaliacaoService;

    @Test
    void feitoPeloPersonalTrueQuandoModalidadePresencial() {
        Personal personal = new Personal();
        Aluno aluno = new Aluno();
        aluno.setPersonal(personal);
        aluno.setModalidade("presencial");

        AvaliacaoFisicaRequest request = new AvaliacaoFisicaRequest(
                1L, LocalDate.now(), 80.0, 1.75, 15.0, "obs", true);

        when(avaliacaoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AvaliacaoFisica av = avaliacaoService.criar(request, aluno);

        assertTrue(av.getFeitoPeloPersonal());
    }

    @Test
    void feitoPeloPersonalFalseQuandoModalidadeOnline() {
        Personal personal = new Personal();
        Aluno aluno = new Aluno();
        aluno.setPersonal(personal);
        aluno.setModalidade("online");

        AvaliacaoFisicaRequest request = new AvaliacaoFisicaRequest(
                1L, LocalDate.now(), 70.0, 1.70, 12.0, "obs", false);

        when(avaliacaoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AvaliacaoFisica av = avaliacaoService.criar(request, aluno);

        assertFalse(av.getFeitoPeloPersonal());
    }
}