package br.edu.ufape.personal_trainer.service;

import java.util.List;
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

@Service
public class ItemTreinoService {

    @Autowired
    private ItemTreinoRepository itemTreinoRepository;

    @Autowired
    private ExercicioRepository exercicioRepository;

    @Autowired
    private DiaTreinoRepository diaTreinoRepository;

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
    public ItemTreino criar(ItemTreinoRequest request, Long diaId) {
        DiaTreino dia = diaTreinoRepository.findById(diaId)
                .orElseThrow(() -> new ResourceNotFoundException("Dia de treino não encontrado"));

        Exercicio exercicio = exercicioRepository.findById(request.exercicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Exercício não encontrado"));

        if (dia.getPlano().getAluno().getPersonal() == null) {
            throw new IllegalArgumentException("Plano pertence a aluno sem personal vinculado");
        }

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
        if (!itemTreinoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Não existe item treino com ID: " + id);
        }
        itemTreinoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ItemTreino> buscarPorExercicioId(Long id) {
        return itemTreinoRepository.findByExercicio_ExercicioId(id);
    }
}