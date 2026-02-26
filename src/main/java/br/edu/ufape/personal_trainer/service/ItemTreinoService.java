package br.edu.ufape.personal_trainer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.ItemTreinoRequest;
import br.edu.ufape.personal_trainer.model.DiaTreino;
import br.edu.ufape.personal_trainer.model.Exercicio;
import br.edu.ufape.personal_trainer.model.ItemTreino;
import br.edu.ufape.personal_trainer.repository.DiaTreinoRepository;
import br.edu.ufape.personal_trainer.repository.ExercicioRepository;
import br.edu.ufape.personal_trainer.repository.ItemTreinoRepository;
import br.edu.ufape.personal_trainer.config.SecurityUtil;

@Service
public class ItemTreinoService {

    @Autowired private ItemTreinoRepository itemTreinoRepository;
    @Autowired private ExercicioRepository exercicioRepository;
    @Autowired private DiaTreinoRepository diaTreinoRepository;

    @Transactional(readOnly = true)
    public ItemTreino buscarId(Long id) {
        SecurityUtil.requireAuthenticated();
        return itemTreinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe item treino com ID: " + id));
    }

    @Transactional
    public ItemTreino criar(ItemTreinoRequest request, Long diaId) {
        SecurityUtil.requireAuthenticated();
        DiaTreino dia = diaTreinoRepository.findById(diaId)
                .orElseThrow(() -> new ResourceNotFoundException("Dia de treino não encontrado"));
        Exercicio exercicio = exercicioRepository.findById(request.exercicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado"));
        if (dia.getPlano().getAluno().getPersonal() == null) {
            throw new IllegalArgumentException("Plano pertence a aluno sem personal vinculado");
        }
        SecurityUtil.requireAdminOrPersonal();
        SecurityUtil.requirePersonalOfPlanoOrAdmin(dia.getPlano(), "Você não pode adicionar item neste treino");
        ItemTreino itemTreino = new ItemTreino();
        itemTreino.setDiaTreino(dia);
        itemTreino.setExercicio(exercicio);
        itemTreino.setSeries(request.series());
        itemTreino.setRepeticoes(request.repeticoes());
        itemTreino.setCargaKg(request.cargaKg());
        itemTreino.setDescansoSegundos(request.descansoSegundos());
        itemTreino = itemTreinoRepository.save(itemTreino);
        dia.getItens().add(itemTreino);
        diaTreinoRepository.save(dia);
        return itemTreino;
    }

    @Transactional
    public void deletar(Long id) {
        SecurityUtil.requireAuthenticated();
        ItemTreino item = itemTreinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe item treino com ID: " + id));
        SecurityUtil.requireAdminOrPersonal();
        SecurityUtil.requirePersonalOfPlanoOrAdmin(item.getDiaTreino().getPlano(), "Acesso negado");
        itemTreinoRepository.deleteById(id);
    }
}