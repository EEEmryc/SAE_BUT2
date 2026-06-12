package sae.learnhub.learnhub.application.User_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public record UserCommand(String nom, String prenom, String email, String password, String role, String statut) {}
    
    public record UserResult(Long id, String nom, String prenom, String email, String role, String statut) {}

    public List<UserResult> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResult).toList();
    }

    @Transactional
    public UserResult createUser(UserCommand command) {
        if (command.password() == null || command.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le mot de passe est obligatoire");
        }
        
        User user = new User();
        user.setNom(command.nom());
        user.setPrenom(command.prenom());
        user.setEmail(command.email());
        user.setRole(command.role());
        user.setStatut(command.statut());
        user.setPassword(passwordEncoder.encode(command.password()));
        
        return toResult(userRepository.save(user));
    }

    @Transactional
    public UserResult updateUser(Long id, UserCommand command) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé avec l'id : " + id));

        user.setNom(command.nom());
        user.setPrenom(command.prenom());
        user.setEmail(command.email());
        user.setRole(command.role());
        user.setStatut(command.statut());

        if (command.password() != null && !command.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(command.password()));
        }

        return toResult(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserResult toResult(User u) {
        return new UserResult(
                u.getId(),
                u.getNom(),
                u.getPrenom(),
                u.getEmail(),
                u.getRole(),
                u.getStatut()
        );
    }
}