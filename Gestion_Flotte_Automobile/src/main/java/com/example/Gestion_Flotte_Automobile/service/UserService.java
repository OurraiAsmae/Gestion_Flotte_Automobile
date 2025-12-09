package com.example.Gestion_Flotte_Automobile.service;

import com.example.Gestion_Flotte_Automobile.entity.User;
import com.example.Gestion_Flotte_Automobile.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(User user);

    User update(Long id, User user);

    void deleteById(Long id);

    Optional<User> findById(Long id);

    List<User> findAll();

    User findByEmail(String email);

    List<User> findByRole(Role role);
}
