package com.example.mdd_backend.services;

import com.example.mdd_backend.models.DBUser;
import com.example.mdd_backend.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        DBUser user = findUserByUsernameOrEmail(usernameOrEmail);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }

    private DBUser findUserByUsernameOrEmail(String usernameOrEmail) {
        DBUser user = userRepository.findByEmail(usernameOrEmail);

        if(user == null) {
            user = userRepository.findByUsername(usernameOrEmail);
        }

        return user;
    }
}
