package br.edu.ufape.personal_trainer.config;

import br.edu.ufape.personal_trainer.model.GrupoMuscular;
import br.edu.ufape.personal_trainer.repository.GrupoMuscularRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DefaultGrupoMuscularLoader implements CommandLineRunner {

    private final GrupoMuscularRepository grupoMuscularRepository;

    public DefaultGrupoMuscularLoader(GrupoMuscularRepository grupoMuscularRepository) {
        this.grupoMuscularRepository = grupoMuscularRepository;
    }

    @Override
    public void run(String... args) {
        List<String> gruposPadrao = Arrays.asList(
            "Peito", "Costas", "Pernas", "Braços", "Ombros", "Abdômen", "Cardio"
        );

        for (String nome : gruposPadrao) {
            if (grupoMuscularRepository.findByNome(nome).isEmpty()) {
                GrupoMuscular grupo = new GrupoMuscular();
                grupo.setNome(nome);
                
                grupo.setDescricao("Exercícios focados na região de " + nome);
                
                grupoMuscularRepository.save(grupo);
                System.out.println("Grupo Muscular pré-cadastrado com sucesso: " + nome);
            }
        }
    }
}