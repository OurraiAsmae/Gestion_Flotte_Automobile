package com.example.Gestion_Flotte_Automobile.service;

import com.example.Gestion_Flotte_Automobile.entity.PasswordResetToken;
import com.example.Gestion_Flotte_Automobile.entity.User;
import com.example.Gestion_Flotte_Automobile.repository.PasswordResetTokenRepository;
import com.example.Gestion_Flotte_Automobile.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public boolean createPasswordResetTokenForUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }

        tokenRepository.deleteByUser(user);
        tokenRepository.flush();
        String token = String.format("%06d", new java.util.Random().nextInt(1000000));
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        tokenRepository.save(myToken);

        emailService.sendEmail(
                user.getEmail(),
                "Réinitialisation de mot de passe",
                "Votre code de réinitialisation est : " + token + "\n\n" +
                        "Utilisez ce code pour changer votre mot de passe.");
        return true;
    }

    public String validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> passToken = tokenRepository.findByToken(token);

        if (passToken.isEmpty()) {
            return "invalidToken";
        }

        if (passToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {
            return "expired";
        }

        return null;
    }

    public User getUserByToken(String token) {
        return tokenRepository.findByToken(token).get().getUser();
    }

    @Transactional
    public void changeUserPassword(User user, String password) {
        user.setMotDePasse(passwordEncoder.encode(password));
        userRepository.save(user);
        tokenRepository.deleteByUser(user);
    }
}
