package br.edu.ufape.personal_trainer.service;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.model.DiaTreino;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.repository.DiaTreinoRepository;
import br.edu.ufape.personal_trainer.repository.PlanoDeTreinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiaTreinoService {

    @Autowired
    private DiaTreinoRepository diaTreinoRepository;

    @Autowired
    private PlanoDeTreinoRepository planoDeTreinoRepository;

    @Transactional(readOnly = true)
    public List<DiaTreino> listarPorPlano(Long planoId) {
        return diaTreinoRepository.findByPlanoPlanoId(planoId);
    }

    @Transactional
    public DiaTreino adicionarDia(Long planoId, DiaTreino diaTreino) {
        PlanoDeTreino plano = planoDeTreinoRepository.findById(planoId)
                .orElseThrow(() -> new ResourceNotFoundException("Plano de treino não encontrado com ID: " + planoId));

        if (diaTreinoRepository.existsByPlanoPlanoIdAndDiaSemana(planoId, diaTreino.getDiaSemana())) {
            throw new IllegalArgumentException("Este dia da semana já existe no plano");
        }

        diaTreino.setPlano(plano);
        return diaTreinoRepository.save(diaTreino);
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