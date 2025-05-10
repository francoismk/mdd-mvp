package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.CreateUserDTO;
import com.example.mdd_backend.models.DBUser;
import com.example.mdd_backend.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        ModelMapper modelMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public void createUser(CreateUserDTO userDTO) {
        DBUser user = modelMapper.map(userDTO, DBUser.class);
    }
}
