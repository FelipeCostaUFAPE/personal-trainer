package br.edu.ufape.personal_trainer.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.FaturaRequest;
import br.edu.ufape.personal_trainer.dto.FaturaResponse;
import br.edu.ufape.personal_trainer.dto.FaturaUpdateRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Fatura;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.service.AlunoService;
import br.edu.ufape.personal_trainer.service.FaturaService;
import br.edu.ufape.personal_trainer.service.PersonalService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/faturas")
public class FaturaController {

    @Autowired private FaturaService faturaService;
    @Autowired private AlunoService alunoService;
    @Autowired private PersonalService personalService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FaturaResponse>> listarTodos() {
        List<FaturaResponse> responses = faturaService.listarTodos()
                .stream()
                .map(FaturaResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FaturaResponse> buscarId(@PathVariable Long id) {
        Fatura fatura = faturaService.buscarId(id);
        return ResponseEntity.ok(new FaturaResponse(fatura));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<FaturaResponse> criar(@Valid @RequestBody FaturaRequest request) {
        Fatura fatura = faturaService.criar(request);
        return ResponseEntity.status(201).body(new FaturaResponse(fatura));
    }
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<FaturaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody FaturaUpdateRequest request) {
        Fatura fatura = faturaService.atualizar(id, request);
        return ResponseEntity.ok(new FaturaResponse(fatura));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        faturaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL','ALUNO')")
    public ResponseEntity<List<FaturaResponse>> buscarPorAlunoId(@PathVariable Long alunoId) {
        List<FaturaResponse> responses = faturaService.buscarPorAlunoId(alunoId)
                .stream()
                .map(FaturaResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL','ALUNO')")
    public ResponseEntity<FaturaResponse> pagarFatura(@PathVariable Long id) {
        Fatura fatura = faturaService.pagarFatura(id);
        return ResponseEntity.ok(new FaturaResponse(fatura));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL','ALUNO')")
    public ResponseEntity<FaturaResponse> cancelarFatura(@PathVariable Long id) {
        Fatura fatura = faturaService.cancelarFatura(id);
        return ResponseEntity.ok(new FaturaResponse(fatura));
    }
    
    @GetMapping("/me")
    @PreAuthorize("hasRole('PERSONAL')")
    public ResponseEntity<List<FaturaResponse>> listarMinhasFaturas(Authentication authentication) {
        String emailLogado = authentication.getName();
        Personal personal = personalService.buscarPorEmail(emailLogado);
        if (personal == null) {
            throw new ResourceNotFoundException("Personal não encontrado");
        }
        List<Aluno> alunosDoPersonal = alunoService.listarAlunosPersonal(personal.getUsuarioId());
        List<Fatura> faturas = new ArrayList<>();
        for (Aluno aluno : alunosDoPersonal) {
            faturas.addAll(faturaService.buscarPorAlunoId(aluno.getUsuarioId()));
        }
        List<FaturaResponse> responses = faturas.stream()
                .map(FaturaResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }
}