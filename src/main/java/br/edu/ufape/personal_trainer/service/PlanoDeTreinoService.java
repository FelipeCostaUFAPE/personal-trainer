package br.edu.ufape.personal_trainer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.PlanoDeTreinoRepository;

@Service
public class PlanoDeTreinoService {

    @Autowired
    private PlanoDeTreinoRepository planoDeTreinoRepository;
    
    @Autowired
    private AlunoRepository alunoRepository;

    // listar todos
    @Transactional(readOnly = true)
    public List<PlanoDeTreino> listarTodos() {
        return planoDeTreinoRepository.findAll();
    }

    // buscar id
    @Transactional(readOnly = true)
    public PlanoDeTreino buscarId(Long id) {
        return planoDeTreinoRepository.findById(id).orElseThrow(() -> new RuntimeException("Plano de treino não encontrado com ID: " + id));
    }
    
    // criar dto
    @Transactional
    public PlanoDeTreino criar(PlanoDeTreinoRequest request) {
        Aluno aluno = alunoRepository.findById(request.alunoId()).orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
        
        if(aluno.getPersonal() == null) {
        	throw new IllegalArgumentException("Aluno precisa estar vinculado a um personal");
        }

        PlanoDeTreino plano = new PlanoDeTreino();
        plano.setAluno(aluno);
        plano.setNome(request.nome());
        plano.setDataInicio(request.dataInicio());
        plano.setDataFim(request.dataFim());

        return planoDeTreinoRepository.save(plano);
    }

    // salvar (TALVEZ ADICIONAR MAIS)
    @Transactional
    public PlanoDeTreino salvar(PlanoDeTreino plano) {
        if (plano.getAluno() == null) {
            throw new IllegalArgumentException("Plano deve ter um aluno");
        }
        if (plano.getNome() == null || plano.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do plano é obrigatório");
        }
        return planoDeTreinoRepository.save(plano);
    }

    // deletar
    @Transactional
    public void deletar(Long id) {
        if (!planoDeTreinoRepository.existsById(id)) {
            throw new RuntimeException("Plano de treino não existe com ID: " + id);
        }
        planoDeTreinoRepository.deleteById(id);
    }

    // metodos personalizados
    @Transactional(readOnly = true)
    public List<PlanoDeTreino> buscarPorAlunoId(Long alunoId) {
        return planoDeTreinoRepository.findByAluno_UsuarioId(alunoId);
    }

}