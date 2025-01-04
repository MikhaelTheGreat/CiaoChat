package org.ciaochat.config.security;

import lombok.RequiredArgsConstructor;
import org.ciaochat.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public IdUserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException(login));
    }
}
