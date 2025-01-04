package org.ciaochat.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationSuccessHandler customSuccessHandler;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(c ->
                c.requestMatchers("/login", "/register", "/perform_registration").permitAll()
                        .anyRequest().authenticated());

        http.formLogin(c -> c.loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .failureUrl("/login?error")
                .successHandler(customSuccessHandler));

        http.headers(headers -> headers
                .contentTypeOptions(Customizer.withDefaults()));

        http.authenticationProvider(authenticationProvider);

        return http.build();
    }
}
