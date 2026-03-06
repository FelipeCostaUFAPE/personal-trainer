package br.edu.ufape.personal_trainer.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.FaturaRequest;
import br.edu.ufape.personal_trainer.enums.StatusFatura;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Fatura;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.FaturaRepository;
import br.edu.ufape.personal_trainer.config.SecurityUtil;

@Service
public class FaturaService {

    @Autowired private FaturaRepository faturaRepository;
    @Autowired private AlunoRepository alunoRepository;

    @Transactional(readOnly = true)
    public List<Fatura> listarTodos() {
        SecurityUtil.requireAuthenticated();
        List<Fatura> faturas = faturaRepository.findAll();
        faturas.forEach(this::verificarVencimento);
        return faturas;
    }

    @Transactional(readOnly = true)
    public Fatura buscarId(Long id) {
        SecurityUtil.requireAuthenticated();
        Fatura fatura = faturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe fatura com ID: " + id));
        verificarVencimento(fatura);
        return fatura;
    }

    @Transactional
    public Fatura criar(FaturaRequest request) {
        SecurityUtil.requireAdminOrPersonal();
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
        if (aluno.getPersonal() == null) {
            throw new IllegalArgumentException("Aluno sem personal vinculado não pode ter fatura");
        }
        SecurityUtil.requirePersonalOfAlunoOrAdmin(aluno, "Personal não pode criar fatura para aluno de outro personal");
        if (faturaRepository.findByAluno_UsuarioIdAndStatus(aluno.getUsuarioId(), StatusFatura.PENDENTE).isPresent()) {
            throw new IllegalStateException("Aluno já possui fatura pendente");
        }
        Fatura fatura = new Fatura();
        fatura.setAluno(aluno);
        fatura.setValor(request.valor());
        fatura.setDataVencimento(request.dataVencimento());
        fatura.setStatus(StatusFatura.PENDENTE);
        return faturaRepository.save(fatura);
    }

    @Transactional
    public void deletar(Long id) {
        SecurityUtil.requireAuthenticated();
        if (!faturaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Não existe fatura com ID: " + id);
        }
        faturaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Fatura> buscarPorAlunoId(Long alunoId) {
        SecurityUtil.requireAuthenticated();
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));
        SecurityUtil.requireAdminPersonalOrAluno();
        if (!SecurityUtil.isAdmin()) {  
            if (SecurityUtil.isPersonal()) {
                SecurityUtil.requirePersonalOfAlunoOrAdmin(aluno, "Você não tem permissão para ver faturas desse aluno");
            } else if (SecurityUtil.isAluno()) {
                SecurityUtil.requireOwnerOrAdmin(aluno.getEmail(), "Você só pode ver suas próprias faturas");
            }
        }
        List<Fatura> faturas = faturaRepository.findByAluno_UsuarioId(alunoId);
        faturas.forEach(this::verificarVencimento);
        return faturas;
    }

    @Transactional
    private void verificarVencimento(Fatura fatura) {
        if (fatura.getStatus() == StatusFatura.PENDENTE && fatura.getDataVencimento().isBefore(LocalDate.now())) {
            fatura.setStatus(StatusFatura.VENCIDA);
            faturaRepository.save(fatura);
        }
    }

    @Transactional
    public Fatura pagarFatura(Long faturaId) {
        SecurityUtil.requireAuthenticated();
        Fatura fatura = faturaRepository.findById(faturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada"));
        if (!SecurityUtil.isAdmin()) {
            if (SecurityUtil.isPersonal()) {
                SecurityUtil.requirePersonalOfAlunoOrAdmin(fatura.getAluno(), "Personal não pode pagar fatura de outro aluno");
            } else if (SecurityUtil.isAluno()) {
                SecurityUtil.requireOwnerOrAdmin(fatura.getAluno().getEmail(), "Aluno não pode pagar fatura de outro aluno");
            }
        }
        if (fatura.getStatus() != StatusFatura.PENDENTE) {
            throw new IllegalStateException("Fatura não está pendente");
        }
        fatura.setStatus(StatusFatura.PAGA);
        fatura.setDataPagamento(LocalDate.now());
        return faturaRepository.save(fatura);
    }

    @Transactional
    public Fatura cancelarFatura(Long faturaId) {
        SecurityUtil.requireAuthenticated();
        Fatura fatura = faturaRepository.findById(faturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada"));
        if (!SecurityUtil.isAdmin()) {
            if (SecurityUtil.isPersonal()) {
                SecurityUtil.requirePersonalOfAlunoOrAdmin(fatura.getAluno(), "Personal não pode cancelar fatura de outro aluno");
            } else if (SecurityUtil.isAluno()) {
                SecurityUtil.requireOwnerOrAdmin(fatura.getAluno().getEmail(), "Aluno não pode cancelar fatura de outro aluno");
            }
        }
        if (fatura.getStatus() != StatusFatura.PENDENTE) {
            throw new IllegalStateException("Só é possível cancelar faturas pendentes");
        }
        fatura.setStatus(StatusFatura.CANCELADA);
        return faturaRepository.save(fatura);
    }
}