package com.lifequest.security;

import com.lifequest.domain.User;
import com.lifequest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        // Permite o login tanto por e-mail quanto pelo nome do personagem
        User user = userRepository.findByEmail(emailOrUsername)
            .orElseGet(() -> userRepository.findByUsername(emailOrUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Herói não encontrado com a credencial fornecida: " + emailOrUsername)));

        return UserPrincipal.from(user);
    }
}