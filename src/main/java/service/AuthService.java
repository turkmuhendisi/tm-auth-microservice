package service;

import configuration.PasswordEncoder;
import controller.UserClient;
import dto.LoginRequest;
import dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;

public class AuthService {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserClient userClient;

    public AuthService(CustomUserDetailsService customUserDetailsService, JwtService jwtService, PasswordEncoder passwordEncoder, UserClient userClient) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userClient = userClient;
    }

    public String login(LoginRequest loginRequest) {
        /*UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getEmail());

        if (!passwordMatches(loginRequest.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid credentials!");
        }

        return jwtService.generateToken(userDetails.getUsername());*/
        ResponseEntity<UserResponse> response = userClient.getUser(loginRequest.getEmail());

        if (response.getStatusCode() == HttpStatus.OK) {
            UserResponse userResponse = response.getBody();
            return jwtService.generateToken(Objects.requireNonNull(userResponse).getEmail());
        }

        throw new BadCredentialsException("Invalid credentials!");
    }

    private boolean passwordMatches(String password, String hashedPassword) {
        return hashedPassword.equals(passwordEncoder.passwordEncoder().encode(password));
    }
}
