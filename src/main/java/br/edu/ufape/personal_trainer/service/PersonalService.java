package br.edu.ufape.personal_trainer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.controller.advice.BusinessValidationException;
import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.PersonalRequest;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.PersonalRepository;

@Service
public class PersonalService {

    @Autowired
    private PersonalRepository personalRepository;

    @Transactional(readOnly = true)
    public List<Personal> listarTodos() {
        return personalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Personal buscarId(Long id) {
        return personalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe personal com ID: " + id));
    }

    @Transactional
    public Personal criar(PersonalRequest request) {
        Map<String, String> erros = new HashMap<>();

        if (personalRepository.findByEmail(request.email()).isPresent()) {
            erros.put("email", "Email já cadastrado");
        }

        if (personalRepository.findByCref(request.cref()).isPresent()) {
            erros.put("cref", "CREF já cadastrado");
        }

        if (!erros.isEmpty()) {
            throw new BusinessValidationException(erros);
        }

        Personal personal = new Personal();
        personal.setNome(request.nome());
        personal.setEmail(request.email());
        personal.setSenha(request.senha());
        personal.setCref(request.cref());
        return personalRepository.save(personal);
    }

    @Transactional
    public void deletar(Long id) {
        Personal personal = buscarId(id);

        if (!personal.getAlunos().isEmpty()) {
            throw new IllegalStateException("Personal possui alunos vinculados — não pode ser deletado");
        }

        personalRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Personal buscarPorCref(String cref) {
        return personalRepository.findByCref(cref)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe personal com CREF: " + cref));
    }

    @Transactional(readOnly = true)
    public Personal buscarPorEmail(String email) {
        return personalRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe personal com EMAIL: " + email));
    }
}