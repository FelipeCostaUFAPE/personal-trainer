package br.edu.ufape.personal_trainer.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.ItemTreinoRequest;
import br.edu.ufape.personal_trainer.model.Exercicio;
import br.edu.ufape.personal_trainer.model.ItemTreino;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.repository.ExercicioRepository;
import br.edu.ufape.personal_trainer.repository.ItemTreinoRepository;
import br.edu.ufape.personal_trainer.repository.PlanoDeTreinoRepository;

@Service
public class ItemTreinoService {

    @Autowired
    private ItemTreinoRepository itemTreinoRepository;

    @Autowired
    private ExercicioRepository exercicioRepository;

    @Autowired
    private PlanoDeTreinoRepository planoDeTreinoRepository;

    @Transactional(readOnly = true)
    public List<ItemTreino> listarTodos() {
        return itemTreinoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ItemTreino buscarId(Long id) {
        return itemTreinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe item treino com ID: " + id));
    }

    @Transactional
    public ItemTreino criar(ItemTreinoRequest request, Long planoId) {
        Exercicio exercicio = exercicioRepository.findById(request.exercicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado"));

        PlanoDeTreino plano = planoDeTreinoRepository.findById(planoId)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado"));

        if (plano.getAluno().getPersonal() == null) {
            throw new IllegalArgumentException("Plano pertence a aluno sem personal vinculado");
        }

        ItemTreino itemTreino = new ItemTreino();
        itemTreino.setExercicio(exercicio);
        itemTreino.setPlano(plano);
        itemTreino.setSeries(request.series());
        itemTreino.setRepeticoes(request.repeticoes());
        itemTreino.setCargaKg(request.cargaKg());
        itemTreino.setDescansoSegundos(request.descansoSegundos());

        itemTreino = itemTreinoRepository.save(itemTreino);

        plano.getItens().add(itemTreino);
        planoDeTreinoRepository.save(plano);

        return itemTreino;
    }

    @Transactional
    public void deletar(Long id) {
        if (!itemTreinoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Não existe item treino com ID: " + id);
        }
        itemTreinoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ItemTreino> buscarPorPlanoId(Long id) {
        return itemTreinoRepository.findByPlano_PlanoId(id);
    }

    @Transactional(readOnly = true)
    public List<ItemTreino> buscarPorExercicioId(Long id) {
        return itemTreinoRepository.findByExercicio_ExercicioId(id);
    }
}