package com.example.Gestion_Flotte_Automobile.service.impl;

import com.example.Gestion_Flotte_Automobile.entity.User;
import com.example.Gestion_Flotte_Automobile.enums.Role;
import com.example.Gestion_Flotte_Automobile.repository.UserRepository;
import com.example.Gestion_Flotte_Automobile.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User save(User user) {
        user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(Long id, User user) {
        return userRepository.findById(id).map(existingUser -> {
            user.setId(id);
            if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
                user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
            } else {
                user.setMotDePasse(existingUser.getMotDePasse());
            }
            return userRepository.save(user);
        }).orElse(null);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findByRole(Role role) {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == role)
                .toList();
    }
}
