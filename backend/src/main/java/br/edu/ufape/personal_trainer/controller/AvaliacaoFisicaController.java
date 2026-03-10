package br.edu.ufape.personal_trainer.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufape.personal_trainer.controller.advice.ResourceNotFoundException;
import br.edu.ufape.personal_trainer.dto.AvaliacaoFisicaRequest;
import br.edu.ufape.personal_trainer.dto.AvaliacaoFisicaResponse;
import br.edu.ufape.personal_trainer.dto.AvaliacaoFisicaUpdateRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.AvaliacaoFisica;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.service.AlunoService;
import br.edu.ufape.personal_trainer.service.AvaliacaoFisicaService;
import br.edu.ufape.personal_trainer.service.PersonalService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoFisicaController {

    @Autowired
    private AvaliacaoFisicaService avaliacaoFisicaService;

    @Autowired
    private AlunoService alunoService;
    
    @Autowired
    private PersonalService personalService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AvaliacaoFisicaResponse>> listarTodos() {
        List<AvaliacaoFisicaResponse> responses = avaliacaoFisicaService.listarTodos()
                .stream()
                .map(AvaliacaoFisicaResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AvaliacaoFisicaResponse> buscarId(@PathVariable Long id) {
        AvaliacaoFisica av = avaliacaoFisicaService.buscarId(id);
        return ResponseEntity.ok(new AvaliacaoFisicaResponse(av));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL','ALUNO')")
    public ResponseEntity<AvaliacaoFisicaResponse> salvar(@Valid @RequestBody AvaliacaoFisicaRequest request) {
        Aluno aluno = alunoService.buscarId(request.alunoId());
        AvaliacaoFisica av = avaliacaoFisicaService.criar(request, aluno);
        return ResponseEntity.status(201).body(new AvaliacaoFisicaResponse(av));
    }
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<AvaliacaoFisicaResponse> atualizar(@PathVariable Long id, @Valid @RequestBody AvaliacaoFisicaUpdateRequest request) {
        AvaliacaoFisica av = avaliacaoFisicaService.atualizar(id, request);
        return ResponseEntity.ok(new AvaliacaoFisicaResponse(av));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        avaliacaoFisicaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL','ALUNO')")
    public ResponseEntity<List<AvaliacaoFisicaResponse>> encontrarPorIdAluno(@PathVariable Long alunoId) {
        List<AvaliacaoFisicaResponse> responses = avaliacaoFisicaService.encontrarPorIdAluno(alunoId)
                .stream()
                .map(AvaliacaoFisicaResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/me")
    @PreAuthorize("hasRole('PERSONAL')")
    public ResponseEntity<List<AvaliacaoFisicaResponse>> listarMinhasAvaliacoes(Authentication authentication) {
        String emailLogado = authentication.getName();
        Personal personal = personalService.buscarPorEmail(emailLogado);
        if (personal == null) {
            throw new ResourceNotFoundException("Personal não encontrado");
        }
        List<Aluno> alunosDoPersonal = alunoService.listarAlunosPersonal(personal.getUsuarioId());
        List<AvaliacaoFisica> avaliacoes = new ArrayList<>();
        for (Aluno aluno : alunosDoPersonal) {
            avaliacoes.addAll(avaliacaoFisicaService.encontrarPorIdAluno(aluno.getUsuarioId()));
        }
        List<AvaliacaoFisicaResponse> responses = avaliacoes.stream()
                .map(AvaliacaoFisicaResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }
}