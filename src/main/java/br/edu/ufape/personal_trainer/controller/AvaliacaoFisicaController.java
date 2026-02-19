package br.edu.ufape.personal_trainer.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import br.edu.ufape.personal_trainer.dto.AvaliacaoFisicaRequest;
import br.edu.ufape.personal_trainer.dto.AvaliacaoFisicaResponse;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.AvaliacaoFisica;
import br.edu.ufape.personal_trainer.service.AlunoService;
import br.edu.ufape.personal_trainer.service.AvaliacaoFisicaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoFisicaController {

    @Autowired
    private AvaliacaoFisicaService avaliacaoFisicaService;

    @Autowired
    private AlunoService alunoService;

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
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL','ALUNO')")
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
}