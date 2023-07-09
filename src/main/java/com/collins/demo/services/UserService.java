package com.collins.demo.services;

import com.collins.demo.entities.Confirmation;
import com.collins.demo.entities.User;
import com.collins.demo.repositories.ConfirmationRepository;
import com.collins.demo.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ConfirmationRepository confirmationRepository;
    private final EmailService emailService;

    public User saveUser(User user){
        if (userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("Email already exists");
        }
        user.setEnabled(false);
        userRepository.save(user);

        Confirmation confirmation = new Confirmation(user);
        confirmationRepository.save(confirmation);

        //sending email with token to user
//        emailService.sendSimpleMailMessage(user.getName(), user.getEmail(), confirmation.getToken());
//        emailService.sendMimeMessageWithAttachments(user.getName(), user.getEmail(), confirmation.getToken());
//        emailService.sendMimeMessageWithEmbeddedImages(user.getName(), user.getEmail(), confirmation.getToken());
//        emailService.sendHtmlEmail(user.getName(), user.getEmail(), confirmation.getToken());
        emailService.sendHtmlEmailWithEmbeddedFiles(user.getName(), user.getEmail(), confirmation.getToken());

        return user;
    }
    public Boolean verifyToken(String token){
        Confirmation confirmation = confirmationRepository.findByToken(token);
        User user = userRepository.findByEmailIgnoreCase(confirmation.getUser().getEmail());
        user.setEnabled(true);
        userRepository.save(user);
//        confirmationRepository.delete(confirmation);
        return Boolean.TRUE;
    }
}
