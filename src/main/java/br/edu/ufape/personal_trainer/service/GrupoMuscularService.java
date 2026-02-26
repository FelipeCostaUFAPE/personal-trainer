package br.edu.ufape.personal_trainer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.controller.advice.BusinessValidationException;
import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.model.GrupoMuscular;
import br.edu.ufape.personal_trainer.repository.GrupoMuscularRepository;
import br.edu.ufape.personal_trainer.config.SecurityUtil;

@Service
public class GrupoMuscularService {

    @Autowired
    private GrupoMuscularRepository grupoMuscularRepository;

    @Transactional(readOnly = true)
    public List<GrupoMuscular> listarTodos() {
        SecurityUtil.requireAuthenticated();
        return grupoMuscularRepository.findAll();
    }

    @Transactional(readOnly = true)
    public GrupoMuscular buscarId(Long id) {
        SecurityUtil.requireAuthenticated();
        return grupoMuscularRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe grupo muscular com ID: " + id));
    }

    @Transactional
    public GrupoMuscular criar(GrupoMuscular grupoMuscular) {
        SecurityUtil.requireAuthenticated();
        if (grupoMuscular.getNome() == null || grupoMuscular.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Um grupo muscular deve ter um nome");
        }
        Map<String, String> erros = new HashMap<>();
        if (grupoMuscularRepository.findByNome(grupoMuscular.getNome()).isPresent()) {
            erros.put("nome", "Nome do grupo muscular já existe");
        }
        if (!erros.isEmpty()) {
            throw new BusinessValidationException(erros);
        }
        return grupoMuscularRepository.save(grupoMuscular);
    }

    @Transactional
    public void deletar(Long id) {
        SecurityUtil.requireAuthenticated();
        if (!grupoMuscularRepository.existsById(id)) {
            throw new ResourceNotFoundException("Não existe grupo muscular com ID: " + id);
        }
        grupoMuscularRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public GrupoMuscular buscarPorNome(String nome) {
        SecurityUtil.requireAuthenticated();
        return grupoMuscularRepository.findByNome(nome)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe grupo muscular com NOME: " + nome));
    }
}