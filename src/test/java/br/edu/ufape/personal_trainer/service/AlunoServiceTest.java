package br.edu.ufape.personal_trainer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import br.edu.ufape.personal_trainer.controller.advice.BusinessValidationException;
import br.edu.ufape.personal_trainer.dto.AlunoRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AlunoService alunoService;

    @Test
    void naoPermiteAlunoComEmailDuplicado() {
        when(alunoRepository.findByEmail("existe@email.com")).thenReturn(Optional.of(new Aluno()));

        AlunoRequest request = new AlunoRequest("Nome", "existe@email.com", "123456", LocalDate.now(), "online", "hipertrofia");

        var ex = assertThrows(BusinessValidationException.class, () -> alunoService.criar(request));
        assertTrue(ex.getErrors().containsKey("email"));
        assertEquals("Email já cadastrado", ex.getErrors().get("email"));

        verify(alunoRepository, never()).save(any());
    }

    @Test
    void permiteCriarAlunoComEmailNovo() {
        when(alunoRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("encoded-123456");
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(i -> i.getArgument(0));

        AlunoRequest request = new AlunoRequest("Novo Aluno", "novo@email.com", "123456", LocalDate.now(), "presencial", "ganho de massa");

        Aluno aluno = alunoService.criar(request);

        assertNotNull(aluno);
        assertEquals("novo@email.com", aluno.getEmail());
        assertEquals("encoded-123456", aluno.getSenha());

        verify(passwordEncoder).encode("123456");
        verify(alunoRepository).save(any());
    }
}