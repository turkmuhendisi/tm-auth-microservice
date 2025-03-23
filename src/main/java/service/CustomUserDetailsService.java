package service;

import controller.UserClient;
import dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserClient userClient;

    public CustomUserDetailsService(UserClient userClient) {
        this.userClient = userClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ResponseEntity<UserResponse> userResponse = userClient.getUser(username);

        if (userResponse == null) {
                throw new UsernameNotFoundException("User not found!");
        }

        List<SimpleGrantedAuthority> authorities = userResponse.getBody().getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new User(
                userResponse.getBody().getEmail(),
                userResponse.getBody().getPassword(),
                authorities
        );
    }
}
