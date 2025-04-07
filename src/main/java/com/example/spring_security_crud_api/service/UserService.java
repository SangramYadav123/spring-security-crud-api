package com.example.spring_security_crud_api.service;

import com.example.spring_security_crud_api.dto.UserDto;
import com.example.spring_security_crud_api.model.User;
import com.example.spring_security_crud_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User registerUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());

        // Assign default role if none provided
        if (userDto.getRoles() == null || userDto.getRoles().isEmpty()) {
            user.setRoles(Collections.singletonList("USER"));
        } else {
            user.setRoles(userDto.getRoles());
        }

        return userRepository.save(user);
    }

    @Transactional
    public Optional<User> updateUser(Long id, UserDto userDto) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setEmail(userDto.getEmail());
                    existingUser.setFullName(userDto.getFullName());

                    // Update password if provided
                    if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    }

                    // Update roles if provided
                    if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
                        existingUser.setRoles(userDto.getRoles());
                    }

                    return userRepository.save(existingUser);
                });
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // Convert User to UserDto (for response)
    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRoles(user.getRoles());
        return dto;
    }

    // Convert UserDto to User (for creation/update)
    public User toEntity(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getFullName());
        user.setRoles(dto.getRoles() != null ? dto.getRoles() : Collections.singletonList("USER"));
        return user;
    }
}