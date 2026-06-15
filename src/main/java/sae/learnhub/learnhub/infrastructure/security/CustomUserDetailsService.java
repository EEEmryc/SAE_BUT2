package sae.learnhub.learnhub.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sae.learnhub.learnhub.domain.model.User;
import sae.learnhub.learnhub.domain.repository.IUserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));

        String role = user.getRole();
        if (role != null && role.startsWith("ROLE_")) {
            role = role.substring(5);
        }
        
        boolean active = !"INACTIF".equalsIgnoreCase(user.getStatut());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                active,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
