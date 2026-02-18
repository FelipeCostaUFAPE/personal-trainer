package br.edu.ufape.personal_trainer.controller;

import br.edu.ufape.personal_trainer.dto.PersonalRequest;
import br.edu.ufape.personal_trainer.dto.PersonalResponse;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.service.PersonalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
}