package br.edu.ufape.personal_trainer.controller;

import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoRequest;
import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoResponse;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.service.PlanoDeTreinoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/planos")
public class PlanoDeTreinoController {

    @Autowired
    private PlanoDeTreinoService planoService;

    @GetMapping
    public ResponseEntity<List<PlanoDeTreinoResponse>> listarTodos() {
        List<PlanoDeTreinoResponse> responses = planoService.listarTodos()
        		.stream()
                .map(PlanoDeTreinoResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanoDeTreinoResponse> buscarId(@PathVariable Long id) {
        PlanoDeTreino plano = planoService.buscarId(id);
        return ResponseEntity.ok(new PlanoDeTreinoResponse(plano));
    }

    @PostMapping
    public ResponseEntity<PlanoDeTreinoResponse> criar(@Valid @RequestBody PlanoDeTreinoRequest request) {
        PlanoDeTreino plano = planoService.criar(request);
        return ResponseEntity.status(201).body(new PlanoDeTreinoResponse(plano));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        planoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<PlanoDeTreinoResponse>> buscarPorAlunoId(@PathVariable Long alunoId) {
        List<PlanoDeTreinoResponse> responses = planoService.buscarPorAlunoId(alunoId)
        		.stream()
                .map(PlanoDeTreinoResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }
}