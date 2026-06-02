package com.project.hotelmanagement.configuration;

import static com.project.hotelmanagement.enums.RoleType.*;
import com.project.hotelmanagement.enums.UserStatus;
import com.project.hotelmanagement.models.Role;
import com.project.hotelmanagement.models.User;
import com.project.hotelmanagement.models.UserHasRole;
import com.project.hotelmanagement.repository.RoleRepository;
import com.project.hotelmanagement.repository.UserHasRoleRepository;
import com.project.hotelmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseConfig {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserHasRoleRepository userHasRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            Role role = roleRepository.findByRoleName(String.valueOf(ADMIN))
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setRoleName(String.valueOf(ADMIN));
                        return roleRepository.save(newRole);
                    });
            User user = userRepository.findByUsername("admin")
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setUsername("admin");
                        newUser.setPassword(passwordEncoder.encode("admin"));
                        newUser.setStatus(UserStatus.ACTIVE);
                        return userRepository.save(newUser);
                    });
            if (!userHasRoleRepository.existsByUserAndRole(user, role)) {
                userHasRoleRepository.save(
                        UserHasRole.builder()
                                .user(user)
                                .role(role)
                                .build());
            }
            log.info("Default admin user created: username=admin, password=admin (please change it!)");
        };
    }
}


