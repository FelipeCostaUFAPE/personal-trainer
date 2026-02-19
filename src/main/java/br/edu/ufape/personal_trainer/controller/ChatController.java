package br.edu.ufape.personal_trainer.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import br.edu.ufape.personal_trainer.dto.ChatRequest;
import br.edu.ufape.personal_trainer.dto.ChatResponse;
import br.edu.ufape.personal_trainer.model.Chat;
import br.edu.ufape.personal_trainer.service.ChatService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired private ChatService chatService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChatResponse>> listarTodos() {
        List<ChatResponse> responses = chatService.listarTodos()
        		.stream()
                .map(ChatResponse::new)
                .toList();
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL','ALUNO')")
    public ResponseEntity<ChatResponse> buscarId(@PathVariable Long id) {
        Chat chat = chatService.buscarId(id);
        return ResponseEntity.ok(new ChatResponse(chat));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL','ALUNO')")
    public ResponseEntity<ChatResponse> criar(@Valid @RequestBody ChatRequest request) {
        Chat chat = chatService.criar(request);
        return ResponseEntity.status(201).body(new ChatResponse(chat));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        chatService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/entre/{alunoId}/{personalId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ChatResponse> buscarPorAlunoIdAndPersonalId(@PathVariable Long alunoId, @PathVariable Long personalId) {
        Chat chat = chatService.buscarPorAlunoIdAndPersonalId(alunoId, personalId);
        return ResponseEntity.ok(new ChatResponse(chat));
    }
}