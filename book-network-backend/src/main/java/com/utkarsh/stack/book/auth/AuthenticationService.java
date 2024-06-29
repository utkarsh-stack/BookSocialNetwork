package com.utkarsh.stack.book.auth;

import com.utkarsh.stack.book.email.EmailService;
import com.utkarsh.stack.book.email.EmailTemplateName;
import com.utkarsh.stack.book.role.RoleRepository;
import com.utkarsh.stack.book.security.JwtService;
import com.utkarsh.stack.book.user.Token;
import com.utkarsh.stack.book.user.TokenRepository;
import com.utkarsh.stack.book.user.User;
import com.utkarsh.stack.book.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    public void register(RegisterationRequest request) throws MessagingException {
        //Fetching USER by default role at the start
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(()-> new IllegalStateException("USER role not initialized"));
        // Creating user data in our database and initially setting enabled flag to false
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);

        //Sending mail validation with token
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        // Generating token
        String activationCode = generateAndSaveActivationToken(user);
        // Sending mail with that token
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                activationCode,
                "Account Activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        // Generating 6 digit random string
        String activationCode = generateActivationCode(6);
        log.info("Activation token generated: {}",activationCode);
        // Storing it in our database mapped to the user created
        Token token = Token.builder()
                .token(activationCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        // Returning the string to be used by mail sender service
        return activationCode;
    }

    private String generateActivationCode(int length) {
        String universe = "0123456789";
        StringBuilder activationToken = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for(int i=0;i<length;i++){
            int index = secureRandom.nextInt(universe.length());
            activationToken.append(universe.charAt(index));
        }
        return activationToken.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();
        var user = ((User)auth.getPrincipal());
        claims.put("fullname", user.getFullName());
        var jwtToken = jwtService.generateToken(claims, ((User)auth.getPrincipal()));
        return AuthenticationResponse.builder()
                .token(jwtToken).build();
    }

    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(()-> new RuntimeException("Invalid token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Token expired, generating new token and sending mail");
        }
        User user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(()-> new RuntimeException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}
