package br.edu.ufape.personal_trainer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.config.SecurityUtil;
import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.AvaliacaoFisicaRequest;
import br.edu.ufape.personal_trainer.dto.AvaliacaoFisicaUpdateRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.AvaliacaoFisica;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.AvaliacaoFisicaRepository;

@Service
public class AvaliacaoFisicaService {

    @Autowired private AvaliacaoFisicaRepository avaliacaoFisicaRepository;
    @Autowired private AlunoRepository alunoRepository;
    
    @Transactional(readOnly = true)
    public List<AvaliacaoFisica> listarTodos() {
        SecurityUtil.requireAuthenticated();
        return avaliacaoFisicaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AvaliacaoFisica buscarId(Long id) {
        SecurityUtil.requireAuthenticated();
        return avaliacaoFisicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe uma Avaliação Física com o ID: " + id));
    }

    @Transactional
    public AvaliacaoFisica criar(AvaliacaoFisicaRequest dto, Aluno aluno) {
        SecurityUtil.requireAdminPersonalOrAluno();

        String modalidade = aluno.getModalidade() != null ? aluno.getModalidade().trim().toLowerCase() : "";
        if (aluno.getPersonal() == null) {
            throw new IllegalArgumentException("Aluno precisa ter um personal vinculado para ter avaliação física");
        }

        if (SecurityUtil.isAdmin()) {
        } else if (SecurityUtil.isPersonal()) {
            SecurityUtil.requirePersonalOfAlunoOrAdmin(aluno, "Personal só pode criar avaliação para alunos presenciais vinculados a si");
            if (!"presencial".equals(modalidade)) {
                throw new IllegalArgumentException("Personal só pode criar avaliação para alunos presenciais");
            }
        } else if (SecurityUtil.isAluno()) {
            SecurityUtil.requireOwnerOrAdmin(aluno.getEmail(), "Aluno só pode criar avaliação própria se for online");
            if (!"online".equals(modalidade)) {
                throw new IllegalArgumentException("Aluno só pode criar avaliação própria se for online");
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
    
    @Transactional
    public AvaliacaoFisica atualizar(Long id, AvaliacaoFisicaUpdateRequest request) {
        SecurityUtil.requireAdminOrPersonal();
        AvaliacaoFisica av = buscarId(id);
        Aluno aluno = av.getAluno();
        SecurityUtil.requirePersonalOfAlunoOrAdmin(aluno, "Você não tem permissão para editar esta avaliação");
        if (request.dataAvaliacao() != null) av.setDataAvaliacao(request.dataAvaliacao());
        if (request.pesoKg() != null) av.setPesoKg(request.pesoKg());
        if (request.alturaCm() != null) av.setAlturaCm(request.alturaCm());
        if (request.percentualGordura() != null) av.setPercentualGordura(request.percentualGordura());
        if (request.observacoes() != null) av.setObservacoes(request.observacoes());
        return avaliacaoFisicaRepository.save(av);
    }

    @Transactional
    public void deletar(Long id) {
        SecurityUtil.requireAuthenticated();
        if (!avaliacaoFisicaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Não existe Avaliação Física com ID: " + id);
        }
        avaliacaoFisicaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoFisica> encontrarPorIdAluno(Long alunoId) {
        SecurityUtil.requireAuthenticated();
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        if (SecurityUtil.isAdmin()) {
            return avaliacaoFisicaRepository.findByAlunoUsuarioId(alunoId);
        }

        if (SecurityUtil.isPersonal()) {
            SecurityUtil.requirePersonalOfAlunoOrAdmin(aluno, "Você não tem permissão para ver avaliações desse aluno");
            return avaliacaoFisicaRepository.findByAlunoUsuarioId(alunoId);
        }

        if (SecurityUtil.isAluno()) {
            SecurityUtil.requireOwnerOrAdmin(aluno.getEmail(), "Você só pode ver suas próprias avaliações");
            return avaliacaoFisicaRepository.findByAlunoUsuarioId(alunoId);
        }

        throw new AccessDeniedException("Acesso negado");
    }
}