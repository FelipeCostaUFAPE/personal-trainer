package br.edu.ufape.personal_trainer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.controller.advice.BusinessValidationException;
import br.edu.ufape.personal_trainer.dto.ExercicioRequest;
import br.edu.ufape.personal_trainer.model.Exercicio;
import br.edu.ufape.personal_trainer.model.GrupoMuscular;
import br.edu.ufape.personal_trainer.repository.ExercicioRepository;
import br.edu.ufape.personal_trainer.repository.GrupoMuscularRepository;

@Service
public class ExercicioService {

	@Autowired
	private ExercicioRepository exercicioRepository;
	
	@Autowired
	private GrupoMuscularRepository grupoMuscularRepository;
	
	// listar todos
	@Transactional(readOnly = true)
	public List<Exercicio> listarTodos(){
		return exercicioRepository.findAll();
	}
	
	// buscar id
	@Transactional(readOnly = true)
	public Exercicio buscarId(Long id) {
		return exercicioRepository.findById(id).orElseThrow(() -> new RuntimeException("Não existe exercicio com ID: " + id));
	}
	
	// criar dto
	@Transactional
	public Exercicio criar(ExercicioRequest request) {
	    Map<String, String> erros = new HashMap<>();
	    
	    if (!exercicioRepository.findByNomeContainingIgnoreCase(request.nome()).isEmpty()) {
	        erros.put("nome", "Já existe um exercício com nome semelhante");
	    }
	    
	    if (!erros.isEmpty()) {
	        throw new BusinessValidationException(erros);
	    }

	    GrupoMuscular grupoMuscular = grupoMuscularRepository.findById(request.grupoMuscularId()).orElseThrow(() -> new RuntimeException("Grupo muscular não encontrado"));

	    Exercicio exercicio = new Exercicio();
	    exercicio.setNome(request.nome());
	    exercicio.setDescricao(request.descricao());
	    exercicio.setGrupoMuscular(grupoMuscular);
	    return exercicioRepository.save(exercicio);
	}
	
	// salvar
	@Transactional
	public Exercicio salvar(Exercicio exercicio) {
	    if (exercicio.getNome() == null || exercicio.getNome().trim().isEmpty()) {
	        throw new IllegalArgumentException("Nome do exercício é obrigatório");
	    }
	    if (exercicio.getGrupoMuscular() == null) {
	        throw new IllegalArgumentException("Grupo muscular é obrigatório");
	    }

	    Map<String, String> erros = new HashMap<>();
	    
	    List<Exercicio> existentes = exercicioRepository.findByNomeContainingIgnoreCase(exercicio.getNome());
	    if (!existentes.isEmpty() && !existentes.get(0).getExercicioId().equals(exercicio.getExercicioId())) {
	        erros.put("nome", "Já existe um exercício com nome semelhante");
	    }
	    
	    if (!erros.isEmpty()) {
	        throw new BusinessValidationException(erros);
	    }

	    return exercicioRepository.save(exercicio);
	}
	
	// deletar
	@Transactional
	public void deletar(Long id) {
		if(!exercicioRepository.existsById(id)) {
			throw new RuntimeException("Não existe exercicio com ID: " + id);
		}
		exercicioRepository.deleteById(id);
	}
	
	// metodos personalizados
	@Transactional(readOnly = true)
	public List<Exercicio> buscarPorGrupoMuscular(Long grupoMuscularId){
		return exercicioRepository.findByGrupoMuscular_GrupoMuscularId(grupoMuscularId);
	}
	
	@Transactional(readOnly = true)
	public List<Exercicio> buscarPorNome(String nome) {
	    return exercicioRepository.findByNomeContainingIgnoreCase(nome);
	}
}
