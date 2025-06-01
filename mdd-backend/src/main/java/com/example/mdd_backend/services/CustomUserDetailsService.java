package com.example.mdd_backend.services;

import com.example.mdd_backend.errors.exceptions.DatabaseOperationException;
import com.example.mdd_backend.errors.exceptions.ResourceNotFoundException;
import com.example.mdd_backend.models.DBUser;
import com.example.mdd_backend.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final static Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        try {
            DBUser user = findUserByUsernameOrEmail(usernameOrEmail);
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .authorities("USER")
                    .build();
        } catch (ResourceNotFoundException e) {
            logger.warn("Error loadUserByUsername: {}", e.getMessage());
            throw new UsernameNotFoundException("User not found");
        } catch (Exception e) {
            logger.error("Error loadUserByUsername: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to load user by username/email");
        }

    }

    private DBUser findUserByUsernameOrEmail(String usernameOrEmail) {
        try {
            DBUser user = userRepository.findByEmail(usernameOrEmail).orElse(null);

            if(user == null) {
                user = userRepository.findByUsername(usernameOrEmail);
            }

            return user;
        } catch (ResourceNotFoundException e) {
          throw e;
        } catch (Exception e) {
            logger.error("Error finding user with username/email {}: {}", usernameOrEmail, e.getMessage(), e);
            throw new DatabaseOperationException("Failed to find user");
        }
    }
}
