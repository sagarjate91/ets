package com.data.ets.service;

import com.data.ets.dto.UserDTO;
import com.data.ets.model.Role;
import com.data.ets.model.User;
import com.data.ets.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String username, String email, String password, String firstName, String lastName, Role role) {
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .enabled(true)
                .build();
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getAllEmployees() {
        return userRepository.findByRole(Role.EMPLOYEE).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setEmail(updatedUser.getEmail());
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setEnabled(updatedUser.getEnabled());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void enableUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setEnabled(true);
            userRepository.save(user);
        });
    }

    public void disableUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setEnabled(false);
            userRepository.save(user);
        });
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().toString())
                .enabled(user.getEnabled())
                .build();
    }
}

