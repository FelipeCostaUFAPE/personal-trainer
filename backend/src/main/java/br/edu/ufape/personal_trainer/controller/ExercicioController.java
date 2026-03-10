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

import br.edu.ufape.personal_trainer.dto.ExercicioRequest;
import br.edu.ufape.personal_trainer.dto.ExercicioResponse;
import br.edu.ufape.personal_trainer.dto.ExercicioUpdateRequest;
import br.edu.ufape.personal_trainer.model.Exercicio;
import br.edu.ufape.personal_trainer.service.ExercicioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exercicios")
public class ExercicioController {

    @Autowired private ExercicioService exercicioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<List<ExercicioResponse>> listarTodos() {
        List<ExercicioResponse> responses = exercicioService.listarTodos()
                .stream()
                .map(ExercicioResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<ExercicioResponse> buscarId(@PathVariable Long id) {
        Exercicio exercicio = exercicioService.buscarId(id);
        return ResponseEntity.ok(new ExercicioResponse(exercicio));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<ExercicioResponse> criar(@Valid @RequestBody ExercicioRequest request) {
        Exercicio exercicio = exercicioService.criar(request);
        return ResponseEntity.status(201).body(new ExercicioResponse(exercicio));
    }
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<ExercicioResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ExercicioUpdateRequest request) {
        Exercicio exercicio = exercicioService.atualizar(id, request);
        return ResponseEntity.ok(new ExercicioResponse(exercicio));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        exercicioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/grupoMuscular/{grupoMuscularId}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<List<ExercicioResponse>> buscarPorGrupoMuscular(@PathVariable Long grupoMuscularId) {
        List<ExercicioResponse> responses = exercicioService.buscarPorGrupoMuscular(grupoMuscularId)
                .stream()
                .map(ExercicioResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/nome")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<List<ExercicioResponse>> buscarPorNome(@RequestParam String nome) {
        List<ExercicioResponse> responses = exercicioService.buscarPorNome(nome)
                .stream()
                .map(ExercicioResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }
}