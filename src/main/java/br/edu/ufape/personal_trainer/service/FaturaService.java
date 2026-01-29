package br.edu.ufape.personal_trainer.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.FaturaRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Fatura;
import br.edu.ufape.personal_trainer.repository.AlunoRepository;
import br.edu.ufape.personal_trainer.repository.FaturaRepository;

@Service
public class FaturaService {

    @Autowired
    private FaturaRepository faturaRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    // listar todos
    @Transactional(readOnly = true)
    public List<Fatura> listarTodos() {
        List<Fatura> faturas = faturaRepository.findAll();
        faturas.forEach(this::verificarVencimento);
        return faturas;
    }

    // buscar id
    @Transactional(readOnly = true)
    public Fatura buscarId(Long id) {
        Fatura fatura = faturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe fatura com ID: " + id));
        verificarVencimento(fatura);
        return fatura;
    }

    // criar dto
    @Transactional
    public Fatura criar(FaturaRequest request) {
        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        if (aluno.getPersonal() == null) {
            throw new IllegalArgumentException("Aluno precisa estar vinculado a um personal");
        }

        if (faturaRepository.findByAluno_UsuarioIdAndStatus(aluno.getUsuarioId(), "PENDENTE").isPresent()) {
            throw new IllegalStateException("Aluno já possui uma fatura pendente");
        }

        Fatura fatura = new Fatura();
        fatura.setAluno(aluno);
        fatura.setValor(request.valor());
        fatura.setDataVencimento(request.dataVencimento());
        fatura.setStatus("PENDENTE");
        return faturaRepository.save(fatura);
    }

    // deletar
    @Transactional
    public void deletar(Long id) {
        if (!faturaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Não existe fatura com ID: " + id);
        }
        faturaRepository.deleteById(id);
    }

    // metodos personalizados
    @Transactional(readOnly = true)
    public List<Fatura> buscarPorAlunoId(Long alunoId) {
        List<Fatura> faturas = faturaRepository.findByAluno_UsuarioId(alunoId);
        faturas.forEach(this::verificarVencimento);
        return faturas;
    }

    @Transactional(readOnly = true)
    public List<Fatura> buscarPorStatus(String status) {
        List<Fatura> faturas = faturaRepository.findByStatus(status);
        faturas.forEach(this::verificarVencimento);
        return faturas;
    }

    @Transactional
    private void verificarVencimento(Fatura fatura) {
        if ("PENDENTE".equals(fatura.getStatus())
                && fatura.getDataVencimento().isBefore(LocalDate.now())) {
            fatura.setStatus("VENCIDA");
            faturaRepository.save(fatura);
        }
    }

    @Transactional
    public Fatura pagarFatura(Long faturaId) {
        Fatura fatura = faturaRepository.findById(faturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada"));

        if (!"PENDENTE".equals(fatura.getStatus())) {
            throw new IllegalStateException("Esta fatura já foi paga, cancelada ou está vencida");
        }

        fatura.setStatus("PAGA");
        fatura.setDataPagamento(LocalDate.now());
        return faturaRepository.save(fatura);
    }

    @Transactional
    public Fatura cancelarFatura(Long faturaId) {
        Fatura fatura = faturaRepository.findById(faturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada"));

        if (!"PENDENTE".equals(fatura.getStatus())) {
            throw new IllegalStateException("Só é possível cancelar faturas pendentes");
        }

        fatura.setStatus("CANCELADA");
        return faturaRepository.save(fatura);
    }
}