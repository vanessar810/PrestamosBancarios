package com.Sistema.PrestamosBancarios.config;

import com.Sistema.PrestamosBancarios.model.Role;
import com.Sistema.PrestamosBancarios.model.User;
import com.Sistema.PrestamosBancarios.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@test.com")) {
            User admin = User.builder()
                    .email("admin@test.com")
                    .username("Admin")
                    .password(passwordEncoder.encode("123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Default admin user created: admin@test.com");
        } else {
            log.info("Admin user already exists, skipping creation");
        }

        if (!userRepository.existsByEmail("usuario@test.com")) {
            User user = User.builder()
                    .email("usuario@test.com")
                    .username("Usuario")
                    .password(passwordEncoder.encode("123"))
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
            log.info("Default user created: usuario@test.com");
        } else {
            log.info("Default user already exists, skipping creation");
        }
    }
}
