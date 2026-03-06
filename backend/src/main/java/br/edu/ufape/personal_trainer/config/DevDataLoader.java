package br.edu.ufape.personal_trainer.config;

import br.edu.ufape.personal_trainer.enums.Role;
import br.edu.ufape.personal_trainer.model.Admin;
import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Personal;
import br.edu.ufape.personal_trainer.repository.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("dev")
public class DevDataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataLoader(UsuarioRepository usuarioRepository,
                         PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        criarAluno();
        criarPersonal();
        criarAdmin();
    }

    private void criarAluno() {
        if (usuarioRepository.findByEmail("aluno@email.com").isPresent()) return;

        Aluno aluno = new Aluno();
        aluno.setNome("Aluno Teste");
        aluno.setEmail("aluno@email.com");
        aluno.setSenha(passwordEncoder.encode("123456"));
        aluno.setRole(Role.ALUNO);
        aluno.setModalidade("Presencial");
        aluno.setObjetivo("Hipertrofia");
        aluno.setDataNascimento(LocalDate.of(2000, 1, 1));
        aluno.setAtivo(true);

        usuarioRepository.save(aluno);
        System.out.println("Aluno criado em ambiente DEV.");
    }

    private void criarPersonal() {
        if (usuarioRepository.findByEmail("personal@email.com").isPresent()) return;

        Personal personal = new Personal();
        personal.setNome("Personal Teste");
        personal.setEmail("personal@email.com");
        personal.setSenha(passwordEncoder.encode("123456"));
        personal.setRole(Role.PERSONAL);
        personal.setCref("123456-SP");

        usuarioRepository.save(personal);
        System.out.println("Personal criado em ambiente DEV.");
    }

    private void criarAdmin() {
        if (usuarioRepository.findByEmail("admin@email.com").isPresent()) return;

        Admin admin = new Admin();
        admin.setNome("Administrador");
        admin.setEmail("admin@email.com");
        admin.setSenha(passwordEncoder.encode("123456"));
        admin.setRole(Role.ADMIN);

        usuarioRepository.save(admin);
        System.out.println("Admin criado em ambiente DEV.");
    }
}
	