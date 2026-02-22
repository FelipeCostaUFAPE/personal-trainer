package br.edu.ufape.personal_trainer.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.AvaliacaoFisicaRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.AvaliacaoFisica;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.AvaliacaoFisicaRepository;

@Service
public class AvaliacaoFisicaService {

    @Autowired
    private AvaliacaoFisicaRepository avaliacaoFisicaRepository;
    
    @Autowired
    private AlunoRepository alunoRepository;

    @Transactional(readOnly = true)
    public AvaliacaoFisica buscarId(Long id) {
        return avaliacaoFisicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe uma Avaliação Física com o ID: " + id));
    }

    @Transactional
    public AvaliacaoFisica criar(AvaliacaoFisicaRequest dto, Aluno aluno) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        String usuarioLogado = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isPersonal = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PERSONAL"));
        boolean isAluno = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO"));

        String modalidade = aluno.getModalidade() != null ? aluno.getModalidade().trim().toLowerCase() : "";

        if (aluno.getPersonal() == null) {
            throw new IllegalArgumentException("Aluno precisa ter um personal vinculado para ter avaliação física");
        }

        if (isAdmin) {
        	
        } else if (isPersonal) {
            if (!"presencial".equals(modalidade) || !aluno.getPersonal().getEmail().equals(usuarioLogado)) {
                throw new IllegalArgumentException("Personal só pode criar avaliação para alunos presenciais vinculados a si");
            }
        } else if (isAluno) {
            if (!"online".equals(modalidade) || !aluno.getEmail().equals(usuarioLogado)) {
                throw new IllegalArgumentException("Aluno só pode criar avaliação própria se for online e tiver personal");
            }
        } else {
            throw new IllegalArgumentException("Apenas admin, personal ou aluno podem criar avaliações");
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

    @Transactional
    public void deletar(Long id) {
        if (!avaliacaoFisicaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Não existe Avaliação Física com ID: " + id);
        }
        avaliacaoFisicaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoFisica> encontrarPorIdAluno(Long alunoId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }

        String usuarioLogado = auth.getName();
        
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isPersonal = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PERSONAL"));
        boolean isAluno = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO"));

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        if (isAdmin) {
            return avaliacaoFisicaRepository.findByAlunoUsuarioId(alunoId);
        }

        if (isPersonal) {
            if (aluno.getPersonal() == null || !aluno.getPersonal().getEmail().equals(usuarioLogado)) {
                throw new AccessDeniedException("Você não tem permissão para ver avaliações desse aluno");
            }
            return avaliacaoFisicaRepository.findByAlunoUsuarioId(alunoId);
        }

        if (isAluno) {
            if (!aluno.getEmail().equals(usuarioLogado)) {
                throw new AccessDeniedException("Você só pode ver suas próprias avaliações");
            }
            return avaliacaoFisicaRepository.findByAlunoUsuarioId(alunoId);
        }

        throw new AccessDeniedException("Acesso negado");
    }
}