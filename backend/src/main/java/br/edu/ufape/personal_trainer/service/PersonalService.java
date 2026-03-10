package br.edu.ufape.personal_trainer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.config.SecurityUtil;
import br.edu.ufape.personal_trainer.controller.advice.BusinessValidationException;
import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.PersonalRequest;
import br.edu.ufape.personal_trainer.dto.PersonalUpdateRequest;
import br.edu.ufape.personal_trainer.enums.Role;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.PersonalRepository;

@Service
public class PersonalService {

    @Autowired private PersonalRepository personalRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Personal> listarTodos() {
        SecurityUtil.requireAuthenticated();
        return personalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Personal buscarId(Long id) {
        SecurityUtil.requireAuthenticated();
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
        personal.setSenha(passwordEncoder.encode(request.senha()));
        personal.setCref(request.cref());
        personal.setRole(Role.PERSONAL);
        return personalRepository.save(personal);
    }
    
    @Transactional
    public Personal atualizar(Long id, PersonalUpdateRequest request) {
        SecurityUtil.requireAdminOrPersonal();
        Personal personal = buscarId(id);
        SecurityUtil.requireOwnerOrAdmin(personal.getEmail(), "Você só pode editar seu próprio perfil");
        if (request.nome() != null) personal.setNome(request.nome());
        if (request.cref() != null) {
            if (personalRepository.findByCref(request.cref()).isPresent() && !personal.getCref().equals(request.cref())) {
                throw new BusinessValidationException(Map.of("cref", "CREF já cadastrado"));
            }
            personal.setCref(request.cref());
        }
        return personalRepository.save(personal);
    }

    @Transactional
    public void deletar(Long id) {
        SecurityUtil.requireAuthenticated();
        Personal personal = buscarId(id);
        if (!personal.getAlunos().isEmpty()) {
            throw new IllegalStateException("Personal possui alunos vinculados — não pode ser deletado");
        }
        personalRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Personal buscarPorCref(String cref) {
        SecurityUtil.requireAuthenticated();
        return personalRepository.findByCref(cref)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe personal com CREF: " + cref));
    }

    @Transactional(readOnly = true)
    public Personal buscarPorEmail(String email) {
        SecurityUtil.requireAuthenticated();
        return personalRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Personal não encontrado com email: " + email));
    }
}