package br.edu.ufape.personal_trainer.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
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
    public List<AvaliacaoFisica> listarTodos() {
        return avaliacaoFisicaRepository.findAll();
    }

    // buscar id
    @Transactional(readOnly = true)
    public AvaliacaoFisica buscarId(Long id) {
        return avaliacaoFisicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe uma Avaliação Física com o ID: " + id));
    }

    // criar dto
    @Transactional
    public AvaliacaoFisica criar(AvaliacaoFisicaRequest dto, Aluno aluno) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = auth.getName();
        String modalidade = aluno.getModalidade() != null ? aluno.getModalidade().trim().toLowerCase() : "";

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isPersonal = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PERSONAL"));

        boolean isAluno = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO"));

        if (!isAdmin) {
            if (isPersonal) {
                if (!"presencial".equals(modalidade) ||
                    !aluno.getPersonal().getEmail().equals(usuarioLogado)) {
                    throw new IllegalArgumentException("Personal não pode criar avaliação para esse aluno");
                }
            }
            if (isAluno) {
                if (!"online".equals(modalidade) ||
                    !aluno.getEmail().equals(usuarioLogado)) {
                    throw new IllegalArgumentException("Aluno não pode criar avaliação para outro aluno");
                }
            }
        }

        AvaliacaoFisica av = new AvaliacaoFisica();
        av.setAluno(aluno);
        av.setDataAvaliacao(dto.dataAvaliacao());
        av.setPesoKg(dto.pesoKg());
        av.setAlturaCm(dto.alturaCm());
        av.setPercentualGordura(dto.percentualGordura());
        av.setObservacoes(dto.observacoes());
        av.setFeitoPeloPersonal("presencial".equals(modalidade));

        return avaliacaoFisicaRepository.save(av);
    }

    // deletar
    @Transactional
    public void deletar(Long id) {
        if (!avaliacaoFisicaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Não existe Avaliação Física com ID: " + id);
        }
        avaliacaoFisicaRepository.deleteById(id);
    }

    // metodos personalizados
    @Transactional(readOnly = true)
    public List<AvaliacaoFisica> encontrarPorIdAluno(Long id) {
        return avaliacaoFisicaRepository.findByAlunoUsuarioId(id);
    }
}