package br.edu.ufape.personal_trainer.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.edu.ufape.personal_trainer.controller.advice.BusinessValidationException;
import br.edu.ufape.personal_trainer.dto.PersonalRequest;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.PersonalRepository;

@ExtendWith(MockitoExtension.class)
class PersonalServiceTest {

    @Mock private PersonalRepository personalRepository;

    @InjectMocks private PersonalService personalService;

    @Test
    void naoPermitePersonalComEmailOuCrefDuplicado() {
        when(personalRepository.findByEmail("existe@email.com")).thenReturn(Optional.of(new Personal()));
        when(personalRepository.findByCref("12345-RJ")).thenReturn(Optional.of(new Personal()));

        PersonalRequest request = new PersonalRequest("Nome", "existe@email.com", "123456", "12345-RJ");

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> personalService.criar(request));

        Map<String, String> errors = ex.getErrors();
        assertEquals(2, errors.size());
        assertEquals("Email já cadastrado", errors.get("email"));
        assertEquals("CREF já cadastrado", errors.get("cref"));

        verify(personalRepository, never()).save(any());
    }

    @Test
    void permiteCriarPersonalComDadosUnicos() {
        when(personalRepository.findByEmail("novo@email.com")).thenReturn(Optional.empty());
        when(personalRepository.findByCref("99999-SP")).thenReturn(Optional.empty());
        when(personalRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        PersonalRequest request = new PersonalRequest("Novo Personal", "novo@email.com", "123456", "99999-SP");

        Personal personal = personalService.criar(request);

        assertNotNull(personal);
        assertEquals("novo@email.com", personal.getEmail());
        assertEquals("99999-SP", personal.getCref());
        verify(personalRepository).save(any());
    }
}