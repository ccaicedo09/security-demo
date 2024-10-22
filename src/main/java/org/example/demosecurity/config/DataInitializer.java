package org.example.demosecurity.config;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.example.demosecurity.model.Role;
import org.example.demosecurity.model.User;
import org.example.demosecurity.repository.RoleRepository;
import org.example.demosecurity.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Role user = roleRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Role not found!"));

        Role editor = roleRepository.findById(2L)
                .orElseThrow(() -> new EntityNotFoundException("Role not found!"));

        Role creator = roleRepository.findById(3L)
                .orElseThrow(() -> new EntityNotFoundException("Role not found!"));

        Role admin = roleRepository.findById(4L)
                .orElseThrow(() -> new EntityNotFoundException("Role not found!"));

        /* Users */
        User adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .isEnabled(true)
                .roles(Set.of(admin))
                .build();

        userRepository.save(adminUser);

        User editorUser = User.builder()
                .username("editor")
                .password(passwordEncoder.encode("editor"))
                .isEnabled(true)
                .roles(Set.of(editor))
                .build();

        userRepository.save(editorUser);

        User creatorUser = User.builder()
                .username("creator")
                .password(passwordEncoder.encode("creator"))
                .isEnabled(true)
                .roles(Set.of(creator))
                .build();

        userRepository.save(creatorUser);

        User userUser = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user"))
                .isEnabled(true)
                .roles(Set.of(user))
                .build();

        userRepository.save(userUser);

        User variousRolesTest = User.builder()
                .username("test")
                .password(passwordEncoder.encode("test"))
                .isEnabled(true)
                .roles(Set.of(user, editor, creator))
                .build();

        userRepository.save(variousRolesTest);
    }
}
