package br.edu.ufape.personal_trainer.controller;

import br.edu.ufape.personal_trainer.model.DiaTreino;
import br.edu.ufape.personal_trainer.service.DiaTreinoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dias")
public class DiaTreinoController {

    @Autowired
    private DiaTreinoService diaTreinoService;

    @GetMapping("/plano/{planoId}")
    public ResponseEntity<List<DiaTreino>> listarPorPlano(@PathVariable Long planoId) {
        List<DiaTreino> dias = diaTreinoService.listarPorPlano(planoId);
        return ResponseEntity.ok(dias);
    }

    @PostMapping("/plano/{planoId}")
    public ResponseEntity<DiaTreino> adicionarDia(@PathVariable Long planoId, @RequestBody DiaTreino diaDeTreino) {
        DiaTreino criado = diaTreinoService.adicionarDia(planoId, diaDeTreino);
        return ResponseEntity.status(201).body(criado);
    }

    @DeleteMapping("/{diaId}")
    public ResponseEntity<Void> removerDia(@PathVariable Long diaId) {
        diaTreinoService.removerDia(diaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaTreino> buscarPorId(@PathVariable Long id) {
        DiaTreino dia = diaTreinoService.buscarPorId(id);
        return ResponseEntity.ok(dia);
    }
}