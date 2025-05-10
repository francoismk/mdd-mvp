package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.CreateUserDTO;
import com.example.mdd_backend.dtos.GetUserDTO;
import com.example.mdd_backend.models.DBUser;
import com.example.mdd_backend.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public List<GetUserDTO> getAllUsers() {
        List<DBUser> users = userRepository.findAll();
        List<GetUserDTO> userDTOs = new ArrayList<>();

        for (DBUser user : users) {
            userDTOs.add(modelMapper.map(user, GetUserDTO.class));
        }

        return userDTOs;
    }

    public GetUserDTO getUserById(UUID id) {
        Optional<DBUser> user = userRepository.findById(id);
        if (user.isPresent()) {
            return modelMapper.map(user, GetUserDTO.class)
        }
        return null;
    }
}
