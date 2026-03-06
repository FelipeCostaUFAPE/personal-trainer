package br.edu.ufape.personal_trainer.service;

import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoCompletoResponse;
import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.repository.PlanoDeTreinoRepository;
import br.edu.ufape.personal_trainer.config.SecurityUtil;

@Service
public class PlanoDeTreinoService {

    @Autowired private PlanoDeTreinoRepository planoDeTreinoRepository;
    @Autowired private AlunoService alunoService;

    @Transactional(readOnly = true)
    public PlanoDeTreino buscarId(Long id) {
        SecurityUtil.requireAuthenticated();
        return planoDeTreinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plano de treino não encontrado com ID: " + id));
    }

    @Transactional
    public PlanoDeTreino criar(PlanoDeTreinoRequest dto) {
        SecurityUtil.requireAuthenticated();
        SecurityUtil.requireAdminOrPersonal();
        Aluno aluno = alunoService.buscarId(dto.alunoId());
        if (aluno.getPersonal() == null) {
            throw new IllegalArgumentException("Aluno precisa estar vinculado a um personal");
        }
        SecurityUtil.requirePersonalOfAlunoOrAdmin(aluno, "Acesso negado");
        PlanoDeTreino plano = new PlanoDeTreino();
        plano.setAluno(aluno);
        plano.setNome(dto.nome());
        plano.setDataInicio(dto.dataInicio());
        plano.setDataFim(dto.dataFim());
        return planoDeTreinoRepository.save(plano);
    }

    @Transactional
    public void deletar(Long id) {
        SecurityUtil.requireAuthenticated();
        if (!planoDeTreinoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plano de treino não existe com ID: " + id);
        }
        planoDeTreinoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PlanoDeTreino> buscarPlanos(Long alunoId) {
        SecurityUtil.requireAuthenticated();
        Aluno aluno = alunoService.buscarId(alunoId);
        SecurityUtil.requireAdminPersonalOrAluno();
        if (!SecurityUtil.isAdmin()) {
            if (SecurityUtil.isPersonal()) {
                SecurityUtil.requirePersonalOfAlunoOrAdmin(aluno, "Você não tem permissão para ver planos desse aluno");
            } else if (SecurityUtil.isAluno()) {
                SecurityUtil.requireOwnerOrAdmin(aluno.getEmail(), "Você só pode ver seus próprios planos");
            }
        }
        return planoDeTreinoRepository.findByAluno_UsuarioId(alunoId);
    }

    @Transactional(readOnly = true)
    public PlanoDeTreinoCompletoResponse buscarPlanoCompleto(Long planoId) {
        SecurityUtil.requireAuthenticated();
        PlanoDeTreino plano = planoDeTreinoRepository.findById(planoId)
                .orElseThrow(() -> new ResourceNotFoundException("Plano de treino não encontrado"));
        Aluno aluno = plano.getAluno();
        SecurityUtil.requireAdminPersonalOrAluno();
        if (!SecurityUtil.isAdmin()) {
            if (SecurityUtil.isPersonal()) {
                SecurityUtil.requirePersonalOfAlunoOrAdmin(aluno, "Você não tem permissão para ver este plano");
            } else if (SecurityUtil.isAluno()) {
                SecurityUtil.requireOwnerOrAdmin(aluno.getEmail(), "Você só pode ver seus próprios planos");
            }
        }
        Hibernate.initialize(plano.getDias());
        plano.getDias().forEach(dia -> Hibernate.initialize(dia.getItens()));
        return new PlanoDeTreinoCompletoResponse(plano);
    }
}