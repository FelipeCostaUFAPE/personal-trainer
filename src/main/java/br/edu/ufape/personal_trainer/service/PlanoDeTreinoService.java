package br.edu.ufape.personal_trainer.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.model.Usuario;
import br.edu.ufape.personal_trainer.repository.PlanoDeTreinoRepository;

@Service
public class PlanoDeTreinoService {

    @Autowired
    private PlanoDeTreinoRepository planoDeTreinoRepository;

    @Autowired
    private AlunoService alunoService;
    
    @Autowired
    private AuthService authService;

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
        Aluno aluno = alunoService.buscarId(dto.alunoId());
        if (aluno.getPersonal() == null) {
            throw new IllegalArgumentException(
                    "Aluno precisa estar vinculado a um personal");
        }

        Usuario usuarioLogado = authService.usuarioLogado();
        if (usuarioLogado.getRole().name().equals("PERSONAL")) {
            if (!aluno.getPersonal().getUsuarioId()
                    .equals(usuarioLogado.getUsuarioId())) {
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

    @Transactional(readOnly = true)
    public List<PlanoDeTreino> buscarPorAluno(Long alunoId) {

        Usuario usuarioLogado = authService.usuarioLogado();
        Aluno aluno = alunoService.buscarId(alunoId);

        if (usuarioLogado.getRole().name().equals("ALUNO")) {
            if (!aluno.getUsuarioId().equals(usuarioLogado.getUsuarioId())) {
                throw new AccessDeniedException("Acesso negado");
            }
        }
        if (usuarioLogado.getRole().name().equals("PERSONAL")) {
            if (!aluno.getPersonal().getUsuarioId().equals(usuarioLogado.getUsuarioId())) {
                throw new AccessDeniedException("Acesso negado");
            }
        }

        return planoDeTreinoRepository.findByAlunoUsuarioId(alunoId);
    }

    @Transactional
    public void deletar(Long id) {
        if (!planoDeTreinoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plano de treino não existe com ID: " + id);
        }
        planoDeTreinoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PlanoDeTreino> buscarPorAlunoId(Long alunoId) {
        return planoDeTreinoRepository.findByAluno_UsuarioId(alunoId);
    }
}