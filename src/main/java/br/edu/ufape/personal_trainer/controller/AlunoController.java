package br.edu.ufape.personal_trainer.controller;

import br.edu.ufape.personal_trainer.dto.AlunoRequest;
import br.edu.ufape.personal_trainer.dto.AlunoResponse;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @GetMapping
    public ResponseEntity<List<AlunoResponse>> listarTodos() {
        List<AlunoResponse> responses = alunoService.listarTodos()
        		.stream()
                .map(AlunoResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> buscarId(@PathVariable Long id) {
        Aluno aluno = alunoService.buscarId(id);
        return ResponseEntity.ok(new AlunoResponse(aluno));
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> salvar(@Valid @RequestBody AlunoRequest request) {
        Aluno aluno = alunoService.criar(request);
        return ResponseEntity.status(201).body(new AlunoResponse(aluno));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        alunoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/modalidade")
    public ResponseEntity<List<AlunoResponse>> listarPorModalidade(@RequestParam String modalidade) {
        List<AlunoResponse> responses = alunoService.listarPorModalidade(modalidade)
        		.stream()
                .map(AlunoResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<AlunoResponse>> listarPorAtivo() {
        List<AlunoResponse> responses = alunoService.listarPorAtivo()
        		.stream()
                .map(AlunoResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/email")
    public ResponseEntity<AlunoResponse> buscarEmail(@RequestParam String email) {
        Aluno aluno = alunoService.buscarEmail(email);
        return ResponseEntity.ok(new AlunoResponse(aluno));
    }

    @PatchMapping("/{alunoId}/vincular/{personalId}")
    public ResponseEntity<AlunoResponse> vincularPersonal(@PathVariable Long alunoId,@PathVariable Long personalId) {
        alunoService.VincularPersonal(alunoId, personalId);
        Aluno aluno = alunoService.buscarId(alunoId);
        return ResponseEntity.ok(new AlunoResponse(aluno));
    }

    @PatchMapping("/{alunoId}/desvincular")
    public ResponseEntity<AlunoResponse> desvincularPersonal(@PathVariable Long alunoId) {
        alunoService.DesvincularPersonal(alunoId);
        Aluno aluno = alunoService.buscarId(alunoId);
        return ResponseEntity.ok(new AlunoResponse(aluno));
    }
}