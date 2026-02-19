package br.edu.ufape.personal_trainer.service;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.DiaTreinoRequest;
import br.edu.ufape.personal_trainer.model.DiaTreino;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.model.Usuario;
import br.edu.ufape.personal_trainer.repository.DiaTreinoRepository;
import br.edu.ufape.personal_trainer.repository.PlanoDeTreinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiaTreinoService {

    @Autowired
    private DiaTreinoRepository diaTreinoRepository;

    @Autowired
    private PlanoDeTreinoRepository planoDeTreinoRepository;
    
    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public List<DiaTreino> listarPorPlano(Long planoId) {
        return diaTreinoRepository.findByPlanoPlanoId(planoId);
    }

    @Transactional
    public DiaTreino adicionarDia(Long planoId, DiaTreinoRequest dto) {
        Usuario usuarioLogado = authService.usuarioLogado();
        PlanoDeTreino plano = planoDeTreinoRepository.findById(planoId)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado"));

        if (usuarioLogado.getRole().name().equals("PERSONAL")) {
            if (!plano.getAluno().getPersonal().getUsuarioId()
                    .equals(usuarioLogado.getUsuarioId())) {
                throw new AccessDeniedException("Acesso negado");
            }
        }
        if (diaTreinoRepository.existsByPlanoPlanoIdAndDiaSemana(planoId, dto.diaSemana())) {
            throw new IllegalArgumentException("Já existe treino para " + dto.diaSemana() + " neste plano");
        }

        DiaTreino dia = new DiaTreino();
        dia.setDiaSemana(dto.diaSemana());
        dia.setPlano(plano);

        return diaTreinoRepository.save(dia);
    }

    @Transactional
    public void removerDia(Long diaId) {
        if (!diaTreinoRepository.existsById(diaId)) {
            throw new ResourceNotFoundException("Dia de treino não encontrado com ID: " + diaId);
        }
        diaTreinoRepository.deleteById(diaId);
    }

    @Transactional(readOnly = true)
    public DiaTreino buscarPorId(Long id) {
        return diaTreinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dia de treino não encontrado com ID: " + id));
    }
}