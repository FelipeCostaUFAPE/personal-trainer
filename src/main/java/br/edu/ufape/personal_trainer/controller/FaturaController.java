package br.edu.ufape.personal_trainer.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.edu.ufape.personal_trainer.dto.FaturaRequest;
import br.edu.ufape.personal_trainer.dto.FaturaResponse;
import br.edu.ufape.personal_trainer.model.Fatura;
import br.edu.ufape.personal_trainer.service.FaturaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/faturas")
public class FaturaController {

    @Autowired
    private FaturaService faturaService;

    @GetMapping
    public ResponseEntity<List<FaturaResponse>> listarTodos() {
        List<FaturaResponse> responses = faturaService.listarTodos()
        		.stream()
                .map(FaturaResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FaturaResponse> buscarId(@PathVariable Long id) {
        Fatura fatura = faturaService.buscarId(id);
        return ResponseEntity.ok(new FaturaResponse(fatura));
    }

    @PostMapping
    public ResponseEntity<FaturaResponse> criar(@Valid @RequestBody FaturaRequest request) {
        Fatura fatura = faturaService.criar(request);
        return ResponseEntity.status(201).body(new FaturaResponse(fatura));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        faturaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<FaturaResponse>> buscarPorAlunoId(@PathVariable Long alunoId) {
        List<FaturaResponse> responses = faturaService.buscarPorAlunoId(alunoId)
        		.stream()
                .map(FaturaResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status")
    public ResponseEntity<List<FaturaResponse>> buscarPorStatus(@RequestParam String status) {
        List<FaturaResponse> responses = faturaService.buscarPorStatus(status)
        		.stream()
                .map(FaturaResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/pagar")
    public ResponseEntity<FaturaResponse> pagarFatura(@PathVariable Long id) {
        Fatura fatura = faturaService.pagarFatura(id);
        return ResponseEntity.ok(new FaturaResponse(fatura));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<FaturaResponse> cancelarFatura(@PathVariable Long id) {
        Fatura fatura = faturaService.cancelarFatura(id);
        return ResponseEntity.ok(new FaturaResponse(fatura));
    }
}