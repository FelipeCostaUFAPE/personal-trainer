package br.edu.ufape.personal_trainer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.controller.advice.BusinessValidationException;
import br.edu.ufape.personal_trainer.dto.AlunoRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.PersonalRepository;

@Service
public class AlunoService {

	@Autowired
	private AlunoRepository alunoRepository;
	
	@Autowired
	private PersonalRepository personalRepository;
	
	// listar todos
	@Transactional(readOnly = true)
	public List<Aluno> listarTodos(){
		return alunoRepository.findAll();
	}
	
	// buscar id
	@Transactional(readOnly = true)
	public Aluno buscarId(Long id) {
		return alunoRepository.findById(id).orElseThrow(() -> new RuntimeException("Não existe um aluno com ID: " + id));
	}
	
	// criar dto
	@Transactional
	public Aluno criar(AlunoRequest request) {
	    Map<String, String> erros = new HashMap<>();
	    
	    if (alunoRepository.findByEmail(request.email()).isPresent()) {
	        erros.put("email", "Email já cadastrado");
	    }
	    
	    if (!erros.isEmpty()) {
	        throw new BusinessValidationException(erros);
	    }

	    Aluno aluno = new Aluno();
	    aluno.setNome(request.nome());
	    aluno.setEmail(request.email());
	    aluno.setSenha(request.senha());
	    aluno.setDataNascimento(request.dataNascimento());
	    aluno.setModalidade(request.modalidade());
	    aluno.setObjetivo(request.objetivo());
	    aluno.setAtivo(false);
	    return alunoRepository.save(aluno);
	}
	
	// salvar
	@Transactional
	public Aluno salvar(Aluno aluno) {
		if(aluno.getEmail() == null || aluno.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentException("Email é obrigatório");
		}
		
		if(aluno.getNome() == null || aluno.getNome().trim().isEmpty()) {
			throw new IllegalArgumentException("Nome é obrigatório");
		}
		return alunoRepository.save(aluno);
	}
	
	// deletar
	@Transactional
	public void deletar(Long id) {
		Aluno aluno = buscarId(id);
		
		if(!alunoRepository.existsById(id)) {
			throw new RuntimeException("Não existe aluno com ID: " + id);
		}
		if (!aluno.getFaturas().isEmpty()) {
	        throw new IllegalStateException("Aluno possui faturas — não pode ser deletado");
	    }
	    if (!aluno.getPlanos().isEmpty()) {
	        throw new IllegalStateException("Aluno possui planos de treino — não pode ser deletado");
	    }
		alunoRepository.deleteById(id);
	}
	
	// metodos personalizados
	@Transactional(readOnly = true)
	public List<Aluno> listarPorModalidade(String modalidade){
		return alunoRepository.findByModalidade(modalidade);
	}
	
	@Transactional(readOnly = true)
	public List<Aluno> listarPorAtivo(){
		return alunoRepository.findByAtivoTrue();
	}
	
	@Transactional(readOnly = true)
	public Aluno buscarEmail(String email){
		return alunoRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Aluno não encontrado com email: " + email));
	}
	
	@Transactional
	public void VincularPersonal(Long alunoId, Long personalId) {
		Aluno aluno = alunoRepository.findById(alunoId).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
		Personal personal = personalRepository.findById(personalId).orElseThrow(() -> new RuntimeException("Personal não encontrado"));
		
		if(aluno.getPersonal() != null) {
			throw new IllegalArgumentException("Aluno já está vinculado a um personal");
		}
		
		aluno.setPersonal(personal);
		aluno.setAtivo(true);
		alunoRepository.save(aluno);
	}
	
	@Transactional
	public void DesvincularPersonal(Long alunoId) {
		Aluno aluno = alunoRepository.findById(alunoId).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
		
		if(aluno.getPersonal() == null) {
			throw new IllegalArgumentException("Aluno não está vinculado a um personal");
		}
		
		aluno.setPersonal(null);
		aluno.setAtivo(false);
		alunoRepository.save(aluno);
	}

}