package br.edu.ufape.personal_trainer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public List<FaturaResponse> listarTodos(){
		return faturaService.listarTodos().stream()
				.map(FaturaResponse::new)
				.toList();
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
    public List<FaturaResponse> buscarPorAlunoId(@PathVariable Long alunoId) {
        return faturaService.buscarPorAlunoId(alunoId).stream()
                .map(FaturaResponse::new)
                .toList();
    }

    @GetMapping("/status")
    public List<FaturaResponse> buscarPorStatus(@RequestParam String status) {
        return faturaService.buscarPorStatus(status).stream()
                .map(FaturaResponse::new)
                .toList();
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
