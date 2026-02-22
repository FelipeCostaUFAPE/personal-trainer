package br.edu.ufape.personal_trainer.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.FaturaRequest;
import br.edu.ufape.personal_trainer.enums.StatusFatura;
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

    @Transactional(readOnly = true)
    public List<Fatura> listarTodos() {
        List<Fatura> faturas = faturaRepository.findAll();
        faturas.forEach(this::verificarVencimento);
        return faturas;
    }

    @Transactional(readOnly = true)
    public Fatura buscarId(Long id) {
        Fatura fatura = faturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Não existe fatura com ID: " + id));
        verificarVencimento(fatura);
        return fatura;
    }

    @Transactional
    public Fatura criar(FaturaRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }
        
        String usuarioLogado = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isPersonal = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PERSONAL"));

        Aluno aluno = alunoRepository.findById(request.alunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado"));

        if (aluno.getPersonal() == null) {
            throw new IllegalArgumentException("Aluno sem personal vinculado não pode ter fatura");
        }

        if (!isAdmin) {
            if (isPersonal) {
                if (!aluno.getPersonal().getEmail().equals(usuarioLogado)) {
                    throw new IllegalArgumentException("Personal não pode criar fatura para aluno de outro personal");
                }
            } else {
                throw new IllegalArgumentException("Apenas admin ou personal podem criar faturas");
            }
        }

        if (faturaRepository
                .findByAluno_UsuarioIdAndStatus(aluno.getUsuarioId(), StatusFatura.PENDENTE)
                .isPresent()) {
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
        if (!faturaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Não existe fatura com ID: " + id);
        }
        faturaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Fatura> buscarPorAlunoId(Long alunoId) {
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
            List<Fatura> faturas = faturaRepository.findByAluno_UsuarioId(alunoId);
            faturas.forEach(this::verificarVencimento);
            return faturas;
        }

        if (isPersonal) {
            if (aluno.getPersonal() == null || !aluno.getPersonal().getEmail().equals(usuarioLogado)) {
                throw new AccessDeniedException("Você não tem permissão para ver faturas desse aluno");
            }
            List<Fatura> faturas = faturaRepository.findByAluno_UsuarioId(alunoId);
            faturas.forEach(this::verificarVencimento);
            return faturas;
        }

        if (isAluno) {
            if (!aluno.getEmail().equals(usuarioLogado)) {
                throw new AccessDeniedException("Você só pode ver suas próprias faturas");
            }
            List<Fatura> faturas = faturaRepository.findByAluno_UsuarioId(alunoId);
            faturas.forEach(this::verificarVencimento);
            return faturas;
        }

        throw new AccessDeniedException("Acesso negado");
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioLogado = auth.getName();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        boolean isPersonal = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PERSONAL"));

        boolean isAluno = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO"));

        Fatura fatura = faturaRepository.findById(faturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada"));

        if (!isAdmin) {
            if (isPersonal) {
                if (!fatura.getAluno().getPersonal().getEmail().equals(usuarioLogado)) {
                    throw new IllegalArgumentException("Personal não pode pagar fatura de outro aluno");
                }
            }
            if (isAluno) {
                if (!fatura.getAluno().getEmail().equals(usuarioLogado)) {
                    throw new IllegalArgumentException("Aluno não pode pagar fatura de outro aluno");
                }
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

        Fatura fatura = faturaRepository.findById(faturaId)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada"));

        if (!isAdmin) {
            if (isPersonal) {
                if (fatura.getAluno().getPersonal() == null ||
                    !fatura.getAluno().getPersonal().getEmail().equals(usuarioLogado)) {
                    throw new IllegalArgumentException("Personal não pode cancelar fatura de outro aluno");
                }
            }
            if (isAluno) {
                if (!fatura.getAluno().getEmail().equals(usuarioLogado)) {
                    throw new IllegalArgumentException("Aluno não pode cancelar fatura de outro aluno");
                }
            }
        }

        if (fatura.getStatus() != StatusFatura.PENDENTE) {
            throw new IllegalStateException("Só é possível cancelar faturas pendentes");
        }

        fatura.setStatus(StatusFatura.CANCELADA);
        return faturaRepository.save(fatura);
    }
}