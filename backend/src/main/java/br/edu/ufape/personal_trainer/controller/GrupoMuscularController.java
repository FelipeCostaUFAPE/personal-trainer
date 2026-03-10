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

import br.edu.ufape.personal_trainer.dto.GrupoMuscularUpdateRequest;
import br.edu.ufape.personal_trainer.model.GrupoMuscular;
import br.edu.ufape.personal_trainer.service.GrupoMuscularService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grupos")
public class GrupoMuscularController {

    @Autowired
    private GrupoMuscularService grupoMuscularService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<List<GrupoMuscular>> listarTodos() {
        return ResponseEntity.ok(grupoMuscularService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<GrupoMuscular> buscarId(@PathVariable Long id) {
        return ResponseEntity.ok(grupoMuscularService.buscarId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GrupoMuscular> criar(@RequestBody GrupoMuscular grupoMuscular) {
        GrupoMuscular salvo = grupoMuscularService.criar(grupoMuscular);
        return ResponseEntity.status(201).body(salvo);
    }
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<GrupoMuscular> atualizar(@PathVariable Long id, @Valid @RequestBody GrupoMuscularUpdateRequest request) {
        GrupoMuscular grupo = grupoMuscularService.atualizar(id, request);
        return ResponseEntity.ok(grupo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        grupoMuscularService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nome")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<GrupoMuscular> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(grupoMuscularService.buscarPorNome(nome));
    }
}