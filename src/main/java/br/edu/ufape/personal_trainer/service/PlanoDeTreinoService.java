package br.edu.ufape.personal_trainer.service;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoCompletoResponse;
import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.repository.PlanoDeTreinoRepository;

@Service
public class PlanoDeTreinoService {

    @Autowired
    private PlanoDeTreinoRepository planoDeTreinoRepository;

    @Autowired
    private AlunoService alunoService;

    @Transactional(readOnly = true)
    public List<PlanoDeTreino> listarTodos() {
        return planoDeTreinoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public PlanoDeTreino buscarId(Long id) {
        return planoDeTreinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plano de treino não encontrado com ID: " + id));
    }

    @Transactional
    public PlanoDeTreino criar(PlanoDeTreinoRequest dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        String emailLogado = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isPersonal = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PERSONAL"));

        if (!isAdmin && !isPersonal) {
            throw new AccessDeniedException("Acesso negado");
        }

        Aluno aluno = alunoService.buscarId(dto.alunoId());
        if (aluno.getPersonal() == null) {
            throw new IllegalArgumentException("Aluno precisa estar vinculado a um personal");
        }

        if (isPersonal) {
            if (!aluno.getPersonal().getEmail().equals(emailLogado)) {
                throw new AccessDeniedException("Acesso negado");
            }
        }

        PlanoDeTreino plano = new PlanoDeTreino();
        plano.setAluno(aluno);
        plano.setNome(dto.nome());
        plano.setDataInicio(dto.dataInicio());
        plano.setDataFim(dto.dataFim());

        return planoDeTreinoRepository.save(plano);
    }

    @Transactional
    public void deletar(Long id) {
        if (!planoDeTreinoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plano de treino não existe com ID: " + id);
        }
        planoDeTreinoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PlanoDeTreino> buscarPlanos(Long alunoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        String emailLogado = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isPersonal = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PERSONAL"));
        boolean isAluno = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO"));

        Aluno aluno = alunoService.buscarId(alunoId);

        if (isAdmin) {
            return planoDeTreinoRepository.findByAluno_UsuarioId(alunoId);
        }

        if (isPersonal) {
            if (aluno.getPersonal() == null || !aluno.getPersonal().getEmail().equals(emailLogado)) {
                throw new AccessDeniedException("Você não tem permissão para ver planos desse aluno");
            }
            return planoDeTreinoRepository.findByAluno_UsuarioId(alunoId);
        }

        if (isAluno) {
            if (!aluno.getEmail().equals(emailLogado)) {
                throw new AccessDeniedException("Você só pode ver seus próprios planos");
            }
            return planoDeTreinoRepository.findByAluno_UsuarioId(alunoId);
        }

        throw new AccessDeniedException("Acesso negado");
    }
    
    @Transactional(readOnly = true)
    public PlanoDeTreinoCompletoResponse buscarPlanoCompleto(Long planoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        String emailLogado = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isPersonal = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PERSONAL"));
        boolean isAluno = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO"));

        PlanoDeTreino plano = planoDeTreinoRepository.findById(planoId)
                .orElseThrow(() -> new ResourceNotFoundException("Plano de treino não encontrado"));

        Aluno aluno = plano.getAluno();

        if (!isAdmin) {
            if (isPersonal) {
                if (aluno.getPersonal() == null || !aluno.getPersonal().getEmail().equals(emailLogado)) {
                    throw new AccessDeniedException("Você não tem permissão para ver este plano");
                }
            } else if (isAluno) {
                if (!aluno.getEmail().equals(emailLogado)) {
                    throw new AccessDeniedException("Você só pode ver seus próprios planos");
                }
            } else {
                throw new AccessDeniedException("Acesso negado");
            }
        }

        Hibernate.initialize(plano.getDias());
        plano.getDias().forEach(dia -> Hibernate.initialize(dia.getItens()));

        return new PlanoDeTreinoCompletoResponse(plano);
    }
}