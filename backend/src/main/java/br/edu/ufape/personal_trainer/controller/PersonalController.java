package br.edu.ufape.personal_trainer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufape.personal_trainer.dto.AlunoResponse;
import br.edu.ufape.personal_trainer.dto.PersonalRequest;
import br.edu.ufape.personal_trainer.dto.PersonalResponse;
import br.edu.ufape.personal_trainer.dto.PersonalUpdateRequest;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.service.PersonalService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/personais")
public class PersonalController {

    @Autowired 
    private PersonalService personalService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PersonalResponse>> listarTodos() {
        List<PersonalResponse> responses = personalService.listarTodos()
                .stream()
                .map(PersonalResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonalResponse> buscarId(@PathVariable Long id) {
        Personal personal = personalService.buscarId(id);
        return ResponseEntity.ok(new PersonalResponse(personal));
    }

    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<PersonalResponse> criar(@Valid @RequestBody PersonalRequest request) {
        Personal personal = personalService.criar(request);
        return ResponseEntity.status(201).body(new PersonalResponse(personal));
    }
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL')")
    public ResponseEntity<PersonalResponse> atualizar(@PathVariable Long id, @Valid @RequestBody PersonalUpdateRequest request) {
        Personal personal = personalService.atualizar(id, request);
        return ResponseEntity.ok(new PersonalResponse(personal));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        personalService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cref")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonalResponse> buscarPorCref(@RequestParam String cref) {
        Personal personal = personalService.buscarPorCref(cref);
        return ResponseEntity.ok(new PersonalResponse(personal));
    }

    @GetMapping("/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonalResponse> buscarPorEmail(@RequestParam String email) {
        Personal personal = personalService.buscarPorEmail(email);
        return ResponseEntity.ok(new PersonalResponse(personal));
    }

    @GetMapping("/me/alunos")
    @PreAuthorize("hasRole('PERSONAL')")
    public ResponseEntity<List<AlunoResponse>> listarMeusAlunos(Authentication authentication) {
        String emailLogado = authentication.getName();
        Personal personal = personalService.buscarPorEmail(emailLogado);
        
        List<AlunoResponse> meusAlunos = personal.getAlunos()
                .stream()
                .map(AlunoResponse::new)
                .toList();
                
        return ResponseEntity.ok(meusAlunos);
    }
    
    @GetMapping("/me")
    @PreAuthorize("hasRole('PERSONAL')")
    public ResponseEntity<PersonalResponse> buscarMeuPerfil(Authentication authentication) {
        String emailLogado = authentication.getName();
        Personal personal = personalService.buscarPorEmail(emailLogado);
        return ResponseEntity.ok(new PersonalResponse(personal));
    }
}