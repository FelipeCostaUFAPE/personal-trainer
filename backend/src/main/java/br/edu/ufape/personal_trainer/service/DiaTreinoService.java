package br.edu.ufape.personal_trainer.service;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.DiaTreinoRequest;
import br.edu.ufape.personal_trainer.dto.DiaTreinoResponse;
import br.edu.ufape.personal_trainer.model.DiaTreino;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.repository.DiaTreinoRepository;
import br.edu.ufape.personal_trainer.repository.PlanoDeTreinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.config.SecurityUtil;
import java.util.List;

@Service
public class DiaTreinoService {

    @Autowired private DiaTreinoRepository diaTreinoRepository;
    @Autowired private PlanoDeTreinoRepository planoDeTreinoRepository;

    @Transactional(readOnly = true)
    public List<DiaTreinoResponse> listarPorPlano(Long planoId) {
        SecurityUtil.requireAuthenticated();
        return diaTreinoRepository.findByPlanoPlanoId(planoId)
                .stream()
                .map(DiaTreinoResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public DiaTreinoResponse buscarPorId(Long id) {
        SecurityUtil.requireAuthenticated();
        DiaTreino dia = diaTreinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dia de treino não encontrado com ID: " + id));
        return new DiaTreinoResponse(dia);
    }

    @Transactional
    public DiaTreino adicionarDia(Long planoId, DiaTreinoRequest dto) {
        SecurityUtil.requireAuthenticated();
        PlanoDeTreino plano = planoDeTreinoRepository.findById(planoId)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado"));
        SecurityUtil.requireAdminOrPersonal();
        SecurityUtil.requirePersonalOfPlanoOrAdmin(plano, "Acesso negado");
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
        SecurityUtil.requireAuthenticated();
        DiaTreino dia = diaTreinoRepository.findById(diaId)
                .orElseThrow(() -> new ResourceNotFoundException("Dia de treino não encontrado com ID: " + diaId));
        SecurityUtil.requireAdminOrPersonal();
        SecurityUtil.requirePersonalOfPlanoOrAdmin(dia.getPlano(), "Acesso negado");
        diaTreinoRepository.deleteById(diaId);
    }
}