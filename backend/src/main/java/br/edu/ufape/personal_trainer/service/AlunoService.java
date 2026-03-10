package br.edu.ufape.personal_trainer.service;

import java.time.LocalDate;
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
import br.edu.ufape.personal_trainer.dto.AlunoRequest;
import br.edu.ufape.personal_trainer.dto.AlunoUpdateRequest;
import br.edu.ufape.personal_trainer.enums.Role;
import br.edu.ufape.personal_trainer.enums.StatusFatura;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.PersonalRepository;

@Service
public class AlunoService {

    @Autowired private AlunoRepository alunoRepository;
    @Autowired private PersonalRepository personalRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Aluno> listarTodos() {
        SecurityUtil.requireAuthenticated();
        return alunoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Aluno buscarId(Long id) {
        SecurityUtil.requireAuthenticated();
        return alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe um aluno com ID: " + id));
    }

    @Transactional
    public Aluno criar(AlunoRequest request) {
        Map<String, String> erros = new HashMap<>();
        if (alunoRepository.findByEmail(request.email()).isPresent()) {
            erros.put("email", "Email já cadastrado");
        }
        if (!erros.isEmpty()) {
            throw new BusinessValidationException(erros);
        }
        Aluno aluno = new Aluno();
        aluno.setNome(request.nome());
        aluno.setEmail(request.email());
        aluno.setSenha(passwordEncoder.encode(request.senha()));
        aluno.setDataNascimento(request.dataNascimento());
        aluno.setModalidade(request.modalidade());
        aluno.setObjetivo(request.objetivo());
        aluno.setAtivo(false);
        aluno.setRole(Role.ALUNO);
        return alunoRepository.save(aluno);
    }
    
    @Transactional
    public Aluno atualizar(Long id, AlunoUpdateRequest request) {
        SecurityUtil.requireAdminOrPersonal();
        Aluno aluno = buscarId(id);
        SecurityUtil.requirePersonalOfAlunoOrAdmin(aluno, "Você só pode editar alunos vinculados a você");
        if (request.nome() != null) aluno.setNome(request.nome());
        if (request.dataNascimento() != null) aluno.setDataNascimento(request.dataNascimento());
        if (request.modalidade() != null) aluno.setModalidade(request.modalidade());
        if (request.objetivo() != null) aluno.setObjetivo(request.objetivo());
        return alunoRepository.save(aluno);
    }

    @Transactional
    public void deletar(Long id) {
        SecurityUtil.requireAuthenticated();
        Aluno aluno = buscarId(id);

        boolean temFaturaPendente = aluno.getFaturas().stream()
                .anyMatch(f -> f.getStatus() == StatusFatura.PENDENTE || f.getStatus() == StatusFatura.VENCIDA);
        if (temFaturaPendente) {
            throw new IllegalStateException("Aluno possui fatura(s) pendente(s) ou vencida(s) — não pode ser deletado");
        }

        boolean temPlanoAtivo = aluno.getPlanos().stream()
                .anyMatch(p -> p.getDataFim().isAfter(LocalDate.now()) || p.getDataFim().isEqual(LocalDate.now()));
        if (temPlanoAtivo) {
            throw new IllegalStateException("Aluno possui plano(s) de treino ativo(s) — não pode ser deletado");
        }

        alunoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Aluno buscarEmail(String email) {
        SecurityUtil.requireAuthenticated();
        return alunoRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com email: " + email));
    }

    @Transactional
    public void vincularPersonal(Long alunoId, Long personalId) {
        SecurityUtil.requireAuthenticated();
        Aluno aluno = buscarId(alunoId);
        if (aluno.getPersonal() != null) {
            throw new IllegalArgumentException("Aluno já está vinculado a um personal");
        }
        Personal personal = personalRepository.findById(personalId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal não encontrado"));

        SecurityUtil.requireAdminOrSpecificPersonal(personal.getEmail(), "Você só pode vincular aluno a você mesmo");

        aluno.setPersonal(personal);
        aluno.setAtivo(true);
        alunoRepository.save(aluno);
    }

    @Transactional
    public void desvincularPersonal(Long alunoId) {
        SecurityUtil.requireAuthenticated();
        Aluno aluno = buscarId(alunoId);
        if (aluno.getPersonal() == null) {
            throw new IllegalArgumentException("Aluno não está vinculado a um personal");
        }

        SecurityUtil.requirePersonalOfAlunoOrAdmin(aluno, "Você só pode desvincular alunos vinculados a você");

        aluno.setPersonal(null);
        aluno.setAtivo(false);
        alunoRepository.save(aluno);
    }

    @Transactional(readOnly = true)
    public List<Aluno> listarAlunosPersonal(Long personalId) {
        SecurityUtil.requireAuthenticated();
        return alunoRepository.findAlunosByPersonalId(personalId);
    }
}