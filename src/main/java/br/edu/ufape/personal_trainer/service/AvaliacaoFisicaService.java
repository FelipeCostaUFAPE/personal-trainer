package br.edu.ufape.personal_trainer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.dto.AvaliacaoFisicaRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.AvaliacaoFisica;
import br.edu.ufape.personal_trainer.repository.AvaliacaoFisicaRepository;

@Service
public class AvaliacaoFisicaService {

	@Autowired
	private AvaliacaoFisicaRepository avaliacaoFisicaRepository;
	
	// listar todos
	@Transactional(readOnly = true)
	public List<AvaliacaoFisica> listarTodos(){
		return avaliacaoFisicaRepository.findAll();
	}
	
	// buscar id
	@Transactional(readOnly = true)
	public AvaliacaoFisica buscarId(Long id) {
		return avaliacaoFisicaRepository.findById(id).orElseThrow(() -> new RuntimeException("Não existe uma Avaliação Fisica com o ID: " + id));
	}
	
	// criar dto
	@Transactional
	public AvaliacaoFisica criar(AvaliacaoFisicaRequest dto, Aluno aluno) {

        if(aluno.getPersonal() == null) {
        	throw new IllegalArgumentException("Aluno precisa estar vinculado a um personal");
        }
		
	    AvaliacaoFisica av = new AvaliacaoFisica();
	    av.setAluno(aluno);
	    av.setDataAvaliacao(dto.dataAvaliacao());
	    av.setPesoKg(dto.pesoKg());
	    av.setAlturaCm(dto.alturaCm());
	    av.setPercentualGordura(dto.percentualGordura());
	    av.setObservacoes(dto.observacoes());
	    
	    String modalidade = aluno.getModalidade() != null ? aluno.getModalidade().trim().toLowerCase() : "";
	    av.setFeitoPeloPersonal("presencial".equals(modalidade));

	    return avaliacaoFisicaRepository.save(av);
	}
	
	// salvar (MUITO PROVAVELMENTE ADICIONAR MAIS)
	@Transactional
	public AvaliacaoFisica salvar(AvaliacaoFisica avaliacaoFisica) {
		if(avaliacaoFisica.getAluno() == null) {
			throw new IllegalArgumentException("Aluno é obrigatório");
		}
		return avaliacaoFisicaRepository.save(avaliacaoFisica);
	}
	
	// deletar
	@Transactional
	public void deletar(Long id) {
		if(!avaliacaoFisicaRepository.existsById(id)) {
			throw new RuntimeException("Não existe Avaliação Física com ID: " + id);
		}
		avaliacaoFisicaRepository.deleteById(id);
	}
	
	// metodos personalizados
	@Transactional(readOnly = true)
	public List<AvaliacaoFisica> encontrarPorIdAluno(Long id){
		return avaliacaoFisicaRepository.findByAlunoUsuarioId(id);
	}
}
