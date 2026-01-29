package br.edu.ufape.personal_trainer.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.edu.ufape.personal_trainer.model.GrupoMuscular;
import br.edu.ufape.personal_trainer.service.GrupoMuscularService;

@RestController
@RequestMapping("/api/grupos")
public class GrupoMuscularController {

    @Autowired
    private GrupoMuscularService grupoMuscularService;

    @GetMapping
    public ResponseEntity<List<GrupoMuscular>> listarTodos() {
        return ResponseEntity.ok(grupoMuscularService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoMuscular> buscarId(@PathVariable Long id) {
        GrupoMuscular grupo = grupoMuscularService.buscarId(id);
        return ResponseEntity.ok(grupo);
    }

    @PostMapping
    public ResponseEntity<GrupoMuscular> criar(@RequestBody GrupoMuscular grupoMuscular) {
        GrupoMuscular salvo = grupoMuscularService.criar(grupoMuscular);
        return ResponseEntity.status(201).body(salvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        grupoMuscularService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/nome")
    public ResponseEntity<GrupoMuscular> buscarPorNome(@RequestParam String nome) {
        GrupoMuscular grupo = grupoMuscularService.buscarPorNome(nome);
        return ResponseEntity.ok(grupo);
    }
}