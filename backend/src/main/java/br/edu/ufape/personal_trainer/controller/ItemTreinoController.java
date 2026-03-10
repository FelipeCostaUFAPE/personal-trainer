package br.edu.ufape.personal_trainer.controller;

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

import br.edu.ufape.personal_trainer.dto.ItemTreinoRequest;
import br.edu.ufape.personal_trainer.dto.ItemTreinoResponse;
import br.edu.ufape.personal_trainer.dto.ItemTreinoUpdateRequest;
import br.edu.ufape.personal_trainer.model.ItemTreino;
import br.edu.ufape.personal_trainer.service.ItemTreinoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/itens")
public class ItemTreinoController {

    @Autowired private ItemTreinoService itemTreinoService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<ItemTreinoResponse> atualizar(@PathVariable Long id, @Valid @RequestBody ItemTreinoUpdateRequest request) {
        ItemTreino item = itemTreinoService.atualizar(id, request);
        return ResponseEntity.ok(new ItemTreinoResponse(item));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        itemTreinoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}