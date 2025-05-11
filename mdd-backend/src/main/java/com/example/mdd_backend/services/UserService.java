package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.CreateUserDTO;
import com.example.mdd_backend.dtos.GetUserDTO;
import com.example.mdd_backend.models.DBUser;
import com.example.mdd_backend.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
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

    public GetUserDTO createUser(CreateUserDTO userDTO) {
        DBUser user = modelMapper.map(userDTO, DBUser.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        DBUser savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, GetUserDTO.class);
    }

    public List<GetUserDTO> getAllUsers() {
        List<DBUser> users = userRepository.findAll();
        return users
            .stream()
            .map(user -> modelMapper.map(user, GetUserDTO.class))
            .collect(Collectors.toList());
    }

    public GetUserDTO getUserById(UUID id) {
        Optional<DBUser> user = userRepository.findById(id);
        if (user.isPresent()) {
            return modelMapper.map(user, GetUserDTO.class);
        }
        return null;
    }
}
