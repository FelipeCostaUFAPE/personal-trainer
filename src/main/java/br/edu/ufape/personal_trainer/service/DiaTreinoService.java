package br.edu.ufape.personal_trainer.service;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.DiaTreinoRequest;
import br.edu.ufape.personal_trainer.dto.DiaTreinoResponse;
import br.edu.ufape.personal_trainer.model.DiaTreino;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.repository.DiaTreinoRepository;
import br.edu.ufape.personal_trainer.repository.PlanoDeTreinoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public List<DiaTreinoResponse> listarPorPlano(Long planoId) {
        return diaTreinoRepository.findByPlanoPlanoId(planoId)
                .stream()
                .map(DiaTreinoResponse::new)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public DiaTreinoResponse buscarPorId(Long id) {
        DiaTreino dia = diaTreinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dia de treino não encontrado com ID: " + id));
        return new DiaTreinoResponse(dia);
    }

    @Transactional
    public DiaTreino adicionarDia(Long planoId, DiaTreinoRequest dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        String emailLogado = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isPersonal = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PERSONAL"));

        PlanoDeTreino plano = planoDeTreinoRepository.findById(planoId)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado"));

        if (!isAdmin && !isPersonal) {
            throw new AccessDeniedException("Acesso negado");
        }

        if (isPersonal) {
            if (plano.getAluno().getPersonal() == null || !plano.getAluno().getPersonal().getEmail().equals(emailLogado)) {
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
        DiaTreino dia = diaTreinoRepository.findById(diaId)
                .orElseThrow(() -> new ResourceNotFoundException("Dia de treino não encontrado com ID: " + diaId));

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

        if (isPersonal) {
            if (dia.getPlano().getAluno().getPersonal() == null || 
                !dia.getPlano().getAluno().getPersonal().getEmail().equals(emailLogado)) {
                throw new AccessDeniedException("Acesso negado");
            }
        }

        diaTreinoRepository.deleteById(diaId);
    }
}