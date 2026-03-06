package br.edu.ufape.personal_trainer.config;

import br.edu.ufape.personal_trainer.enums.Role;
import br.edu.ufape.personal_trainer.model.Admin;
import br.edu.ufape.personal_trainer.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!dev")
public class DefaultAdminLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultAdminLoader(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByEmail("admin@email.com").isEmpty()) {
            Admin admin = new Admin();
            admin.setNome("Administrador");
            admin.setEmail("admin@email.com");
            admin.setSenha(passwordEncoder.encode("123456"));
            admin.setRole(Role.ADMIN);
            usuarioRepository.save(admin);
            System.out.println("ADMIN criado no perfil DEFAULT.");
        } else {
            System.out.println("ADMIN já existe no banco.");
        }
    }
}