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
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoCompletoResponse;
import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoRequest;
import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoResponse;
import br.edu.ufape.personal_trainer.dto.PlanoDeTreinoUpdateRequest;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;
import br.edu.ufape.personal_trainer.service.PlanoDeTreinoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/planos")
public class PlanoDeTreinoController {

    @Autowired private PlanoDeTreinoService planoService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanoDeTreinoResponse> buscarId(@PathVariable Long id) {
        PlanoDeTreino plano = planoService.buscarId(id);
        return ResponseEntity.ok(new PlanoDeTreinoResponse(plano));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<PlanoDeTreinoResponse> criar(@Valid @RequestBody PlanoDeTreinoRequest request) {
        PlanoDeTreino plano = planoService.criar(request);
        return ResponseEntity.status(201).body(new PlanoDeTreinoResponse(plano));
    }
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<PlanoDeTreinoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody PlanoDeTreinoUpdateRequest request) {
        PlanoDeTreino plano = planoService.atualizar(id, request);
        return ResponseEntity.ok(new PlanoDeTreinoResponse(plano));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        planoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL','ALUNO')")
    public ResponseEntity<List<PlanoDeTreinoResponse>> buscarPorAlunoId(@PathVariable Long alunoId) {
        List<PlanoDeTreinoResponse> responses = planoService.buscarPlanos(alunoId)
                .stream()
                .map(PlanoDeTreinoResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{planoId}/completo")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL','ALUNO')")
    public ResponseEntity<PlanoDeTreinoCompletoResponse> buscarPlanoCompleto(@PathVariable Long planoId) {
        PlanoDeTreinoCompletoResponse response = planoService.buscarPlanoCompleto(planoId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<List<PlanoDeTreinoResponse>> listarTodos() {
        List<PlanoDeTreinoResponse> responses = planoService.listarTodos()
                .stream()
                .map(PlanoDeTreinoResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }
}