package br.edu.ufape.personal_trainer.controller;

import br.edu.ufape.personal_trainer.dto.DiaTreinoRequest;
import br.edu.ufape.personal_trainer.dto.DiaTreinoResponse;
import br.edu.ufape.personal_trainer.model.DiaTreino;
import br.edu.ufape.personal_trainer.service.DiaTreinoService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dias")
public class DiaTreinoController {

    @Autowired
    private DiaTreinoService diaTreinoService;

    @GetMapping("/plano/{planoId}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<List<DiaTreinoResponse>> listarPorPlano(@PathVariable Long planoId) {
        List<DiaTreinoResponse> dias = diaTreinoService.listarPorPlano(planoId);
        return ResponseEntity.ok(dias);
    }

    @PostMapping("/plano/{planoId}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<DiaTreinoResponse> adicionar(@PathVariable Long planoId, @RequestBody @Valid DiaTreinoRequest request) {
        DiaTreino dia = diaTreinoService.adicionarDia(planoId, request);
        return ResponseEntity.status(201).body(new DiaTreinoResponse(dia));
    }

    @DeleteMapping("/{diaId}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<Void> removerDia(@PathVariable Long diaId) {
        diaTreinoService.removerDia(diaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<DiaTreinoResponse> buscarPorId(@PathVariable Long id) {
        DiaTreinoResponse dia = diaTreinoService.buscarPorId(id);
        return ResponseEntity.ok(dia);
    }
}