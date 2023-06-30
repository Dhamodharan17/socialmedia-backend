package com.socialmediabackend.service;

import com.socialmediabackend.model.ConfirmationToken;
import com.socialmediabackend.model.User;
import com.socialmediabackend.repository.ConfirmationTokenRepository;
import com.socialmediabackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    EmailService emailService;

    @Override
    public ResponseEntity<?> saveUser(User user) {

        if(userRepository.existsByUserEmail(user.getUserEmail())){
            return ResponseEntity.badRequest().body("Error: Email is already in use");
        }

        userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getUserEmail());
        mailMessage.setSubject("Complete Registration");
        mailMessage.setText("To confirm your account, please click here : "
        +"http://localhost:8080/confirm-account?token="+confirmationToken.getConfirmationToken()
        );
        emailService.sendEmail(mailMessage);

        return ResponseEntity.ok("Verify email by the link sent on your email address");

    }

    @Override
    public ResponseEntity<?> confirmEmail(String confirmationToken) {
      ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
      if(token != null){
          User user = userRepository.findByUserEmailIgnoreCase(token.getUser().getUserEmail());
          user.setIsEnabled(true);
          userRepository.save(user);
          return ResponseEntity.ok("Email verified successfully!");
      }
      return ResponseEntity.badRequest().body("Error: Couldn't verify email");
    }

}
