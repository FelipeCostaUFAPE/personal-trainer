package br.edu.ufape.personal_trainer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.config.SecurityUtil;
import br.edu.ufape.personal_trainer.controller.advice.BusinessValidationException;
import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.ExercicioRequest;
import br.edu.ufape.personal_trainer.dto.ExercicioUpdateRequest;
import br.edu.ufape.personal_trainer.model.Exercicio;
import br.edu.ufape.personal_trainer.model.GrupoMuscular;
import br.edu.ufape.personal_trainer.repository.ExercicioRepository;
import br.edu.ufape.personal_trainer.repository.GrupoMuscularRepository;

@Service
public class ExercicioService {

    @Autowired private ExercicioRepository exercicioRepository;
    @Autowired private GrupoMuscularRepository grupoMuscularRepository;

    @Transactional(readOnly = true)
    public List<Exercicio> listarTodos() {
        SecurityUtil.requireAuthenticated();
        return exercicioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Exercicio buscarId(Long id) {
        SecurityUtil.requireAuthenticated();
        return exercicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe exercício com ID: " + id));
    }

    @Transactional
    public Exercicio criar(ExercicioRequest request) {
        SecurityUtil.requireAuthenticated();
        Map<String, String> erros = new HashMap<>();
        if (!exercicioRepository.findByNomeContainingIgnoreCase(request.nome()).isEmpty()) {
            erros.put("nome", "Já existe um exercício com nome semelhante");
        }
        if (!erros.isEmpty()) {
            throw new BusinessValidationException(erros);
        }
        GrupoMuscular grupoMuscular = grupoMuscularRepository.findById(request.grupoMuscularId())
                .orElseThrow(() -> new ResourceNotFoundException("Grupo muscular não encontrado"));
        Exercicio exercicio = new Exercicio();
        exercicio.setNome(request.nome());
        exercicio.setDescricao(request.descricao());
        exercicio.setGrupoMuscular(grupoMuscular);
        return exercicioRepository.save(exercicio);
    }
    
    @Transactional
    public Exercicio atualizar(Long id, ExercicioUpdateRequest request) {
        SecurityUtil.requireAdminOrPersonal();
        Exercicio exercicio = buscarId(id);
        if (request.nome() != null) exercicio.setNome(request.nome());
        if (request.descricao() != null) exercicio.setDescricao(request.descricao());
        return exercicioRepository.save(exercicio);
    }

    @Transactional
    public void deletar(Long id) {
        SecurityUtil.requireAuthenticated();
        if (!exercicioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Não existe exercício com ID: " + id);
        }
        exercicioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Exercicio> buscarPorGrupoMuscular(Long grupoMuscularId) {
        SecurityUtil.requireAuthenticated();
        return exercicioRepository.findByGrupoMuscular_GrupoMuscularId(grupoMuscularId);
    }

    @Transactional(readOnly = true)
    public List<Exercicio> buscarPorNome(String nome) {
        SecurityUtil.requireAuthenticated();
        return exercicioRepository.findByNomeContainingIgnoreCase(nome);
    }
}