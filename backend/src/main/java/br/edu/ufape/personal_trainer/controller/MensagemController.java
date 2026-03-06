package br.edu.ufape.personal_trainer.controller;

import br.edu.ufape.personal_trainer.dto.MensagemRequest;
import br.edu.ufape.personal_trainer.dto.MensagemResponse;
import br.edu.ufape.personal_trainer.model.Mensagem;
import br.edu.ufape.personal_trainer.service.MensagemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mensagens")
public class MensagemController {

    @Autowired private MensagemService mensagemService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MensagemResponse> buscarId(@PathVariable Long id) {
        Mensagem msg = mensagemService.buscarId(id);
        return ResponseEntity.ok(new MensagemResponse(msg));
    }

    @PostMapping("/chat/{chatId}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL','ALUNO')")
    public ResponseEntity<MensagemResponse> enviar(@PathVariable Long chatId, @Valid @RequestBody MensagemRequest request) {
        Mensagem msg = mensagemService.criar(request, chatId);
        return ResponseEntity.status(201).body(new MensagemResponse(msg));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        mensagemService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chat/{chatId}")
    @PreAuthorize("hasAnyRole('ADMIN','PERSONAL','ALUNO')")
    public ResponseEntity<List<MensagemResponse>> buscarPorChatId(@PathVariable Long chatId) {
        List<MensagemResponse> responses = mensagemService.buscarPorChatId(chatId)
                .stream()
                .map(MensagemResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }
}