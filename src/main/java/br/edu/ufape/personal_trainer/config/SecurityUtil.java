package br.edu.ufape.personal_trainer.config;

import br.edu.ufape.personal_trainer.model.Aluno;
import br.edu.ufape.personal_trainer.model.Chat;
import br.edu.ufape.personal_trainer.model.PlanoDeTreino;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private static Authentication getAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new IllegalStateException("Usuário não autenticado");
        }
        return auth;
    }

    public static String getCurrentEmail() {
        return getAuth().getName();
    }

    public static boolean isAdmin() {
        return getAuth().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public static boolean isPersonal() {
        return getAuth().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PERSONAL"));
    }

    public static boolean isAluno() {
        return getAuth().getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ALUNO"));
    }

    public static void requireAuthenticated() {
        getAuth();
    }

    public static void requireAdminOrPersonal() {
        if (!isAdmin() && !isPersonal()) {
            throw new AccessDeniedException("Acesso negado: apenas admin ou personal");
        }
    }

    public static void requireAdminPersonalOrAluno() {
        if (!isAdmin() && !isPersonal() && !isAluno()) {
            throw new AccessDeniedException("Acesso negado: apenas admin, personal ou aluno");
        }
    }

    public static void requireOwnerOrAdmin(String recursoEmail, String errorMessage) {
        if (!isAdmin()) {
            String emailLogado = getCurrentEmail();
            if (!recursoEmail.equals(emailLogado)) {
                throw new AccessDeniedException(errorMessage);
            }
        }
    }

    public static void requirePersonalOfAlunoOrAdmin(Aluno aluno, String errorMessage) {
        if (!isAdmin()) {
            String emailLogado = getCurrentEmail();
            if (aluno.getPersonal() == null || !aluno.getPersonal().getEmail().equals(emailLogado)) {
                throw new AccessDeniedException(errorMessage);
            }
        }
    }

    public static void requireAdminOrSpecificPersonal(String targetPersonalEmail, String errorMessage) {
        if (!isAdmin()) {
            String emailLogado = getCurrentEmail();
            if (!targetPersonalEmail.equals(emailLogado)) {
                throw new IllegalStateException(errorMessage);
            }
        }
    }
    
    public static void requirePersonalOfPlanoOrAdmin(PlanoDeTreino plano, String errorMessage) {
        if (!isAdmin()) {
            String emailLogado = getCurrentEmail();
            Aluno aluno = plano.getAluno();
            if (aluno == null || aluno.getPersonal() == null || !aluno.getPersonal().getEmail().equals(emailLogado)) {
                throw new AccessDeniedException(errorMessage);
            }
        }
    }
    
    public static void requireParticipantOfChatOrAdmin(Chat chat, String errorMessage) {
        if (!isAdmin()) {
            String emailLogado = getCurrentEmail();
            boolean ehAluno = chat.getAluno().getEmail().equals(emailLogado);
            boolean ehPersonal = chat.getPersonal().getEmail().equals(emailLogado);
            if (!ehAluno && !ehPersonal) {
                throw new AccessDeniedException(errorMessage);
            }
        }
    }
}