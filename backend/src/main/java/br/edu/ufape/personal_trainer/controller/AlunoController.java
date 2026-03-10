package br.edu.ufape.personal_trainer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufape.personal_trainer.dto.AlunoRequest;
import br.edu.ufape.personal_trainer.dto.AlunoResponse;
import br.edu.ufape.personal_trainer.dto.AlunoUpdateRequest;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.service.AlunoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AlunoResponse>> listarTodos() {
        List<AlunoResponse> responses = alunoService.listarTodos()
        		.stream()
                .map(AlunoResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlunoResponse> buscarId(@PathVariable Long id) {
        Aluno aluno = alunoService.buscarId(id);
        return ResponseEntity.ok(new AlunoResponse(aluno));
    }

    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<AlunoResponse> criar(@Valid @RequestBody AlunoRequest request) {
        Aluno aluno = alunoService.criar(request);
        return ResponseEntity.status(201).body(new AlunoResponse(aluno));
    }
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<AlunoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody AlunoUpdateRequest request) {
        Aluno aluno = alunoService.atualizar(id, request);
        return ResponseEntity.ok(new AlunoResponse(aluno));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        alunoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AlunoResponse> buscarEmail(@RequestParam String email) {
        Aluno aluno = alunoService.buscarEmail(email);
        return ResponseEntity.ok(new AlunoResponse(aluno));
    }

    @PatchMapping("/{alunoId}/vincular/{personalId}")
    @PreAuthorize("hasAnyRole('PERSONAL','ADMIN')")
    public ResponseEntity<AlunoResponse> vincularPersonal(@PathVariable Long alunoId, @PathVariable Long personalId) {
        alunoService.vincularPersonal(alunoId, personalId);
        Aluno aluno = alunoService.buscarId(alunoId);
        return ResponseEntity.ok(new AlunoResponse(aluno));
    }

    @PatchMapping("/{alunoId}/desvincular")
    @PreAuthorize("hasAnyRole('PERSONAL','ADMIN')")
    public ResponseEntity<AlunoResponse> desvincularPersonal(@PathVariable Long alunoId) {
        alunoService.desvincularPersonal(alunoId);
        Aluno aluno = alunoService.buscarId(alunoId);
        return ResponseEntity.ok(new AlunoResponse(aluno));
    }
}