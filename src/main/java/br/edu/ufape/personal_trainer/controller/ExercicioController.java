package br.edu.ufape.personal_trainer.controller;

import br.edu.ufape.personal_trainer.dto.ExercicioRequest;
import br.edu.ufape.personal_trainer.dto.ExercicioResponse;
import br.edu.ufape.personal_trainer.model.Exercicio;
import br.edu.ufape.personal_trainer.service.ExercicioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/exercicios")
public class ExercicioController {

    @Autowired
    private ExercicioService exercicioService;

    @GetMapping
    public ResponseEntity<List<ExercicioResponse>> listarTodos() {
        List<ExercicioResponse> responses = exercicioService.listarTodos()
        		.stream()
                .map(ExercicioResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExercicioResponse> buscarId(@PathVariable Long id) {
        Exercicio exercicio = exercicioService.buscarId(id);
        return ResponseEntity.ok(new ExercicioResponse(exercicio));
    }

    @PostMapping
    public ResponseEntity<ExercicioResponse> criar(@Valid @RequestBody ExercicioRequest request) {
        Exercicio exercicio = exercicioService.criar(request);
        return ResponseEntity.status(201).body(new ExercicioResponse(exercicio));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        exercicioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/grupoMuscular/{grupoMuscularId}")
    public ResponseEntity<List<ExercicioResponse>> buscarPorGrupoMuscular(@PathVariable Long grupoMuscularId) {
        List<ExercicioResponse> responses = exercicioService.buscarPorGrupoMuscular(grupoMuscularId)
        		.stream()
                .map(ExercicioResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/nome")
    public ResponseEntity<List<ExercicioResponse>> buscarPorNome(@RequestParam String nome) {
        List<ExercicioResponse> responses = exercicioService.buscarPorNome(nome)
        		.stream()
                .map(ExercicioResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }
}