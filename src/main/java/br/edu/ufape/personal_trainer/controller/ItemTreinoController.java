package br.edu.ufape.personal_trainer.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import br.edu.ufape.personal_trainer.dto.ItemTreinoRequest;
import br.edu.ufape.personal_trainer.dto.ItemTreinoResponse;
import br.edu.ufape.personal_trainer.model.ItemTreino;
import br.edu.ufape.personal_trainer.service.ItemTreinoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/itens")
public class ItemTreinoController {

    @Autowired
    private ItemTreinoService itemTreinoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<ItemTreinoResponse>> listarTodos() {
        List<ItemTreinoResponse> responses = itemTreinoService.listarTodos()
        		.stream()
                .map(ItemTreinoResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ItemTreinoResponse> buscarId(@PathVariable Long id) {
        ItemTreino itemTreino = itemTreinoService.buscarId(id);
        return ResponseEntity.ok(new ItemTreinoResponse(itemTreino));
    }

    @PostMapping("/dia/{diaId}/itens")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<ItemTreinoResponse> criar(@PathVariable Long diaId, @Valid @RequestBody ItemTreinoRequest request) {
        ItemTreino item = itemTreinoService.criar(request, diaId);
        return ResponseEntity.status(201).body(new ItemTreinoResponse(item));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        itemTreinoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exercicio/{exercicioId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<ItemTreinoResponse>> buscarPorExercicioId(@PathVariable Long exercicioId) {
        List<ItemTreinoResponse> responses = itemTreinoService.buscarPorExercicioId(exercicioId)
        		.stream()
                .map(ItemTreinoResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }
}