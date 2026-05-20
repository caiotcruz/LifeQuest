package com.lifequest.security;

import com.lifequest.domain.User;
import com.lifequest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Atualizado para usar o método corrigido com parâmetro único do Passo 2
        User user = userRepository.findByEmailOrUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Usuário não encontrado com o identificador: " + username));

        // Retorna o nosso custom UserPrincipal contendo o ID
        return UserPrincipal.create(user);
    }
}