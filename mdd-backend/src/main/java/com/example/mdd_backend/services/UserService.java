package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.CreateUserDTO;
import com.example.mdd_backend.dtos.GetThemeDTO;
import com.example.mdd_backend.dtos.GetUserDTO;
import com.example.mdd_backend.models.DBTheme;
import com.example.mdd_backend.models.DBUser;
import com.example.mdd_backend.repositories.ThemeRepository;
import com.example.mdd_backend.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserService(
        UserRepository userRepository,
        ThemeRepository themeRepository,
        PasswordEncoder passwordEncoder,
        ModelMapper modelMapper
    ) {
        this.userRepository = userRepository;
        this.themeRepository = themeRepository;
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
            .map(user -> {
                return buildUserDto(user);
            })
            .collect(Collectors.toList());
    }

    public GetUserDTO getUserById(String id) {
        DBUser user = userRepository
            .findById(id)
            .orElseThrow(() ->
                new NoSuchElementException("user not found with ID : " + id)
            );

        return buildUserDto(user);
    }

    public GetUserDTO getUserByEmail(String email) {
        DBUser user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new NoSuchElementException("user not found with email : " + email)
            );

        return buildUserDto(user);
    }

    private GetUserDTO buildUserDto(DBUser user) {
        GetUserDTO userDTO = modelMapper.map(user, GetUserDTO.class);

        if (
            user.getSubscriptions() != null &&
            !user.getSubscriptions().isEmpty()
        ) {
            List<GetThemeDTO> themeDTOs = user
                .getSubscriptions()
                .stream()
                .map(themeId -> {
                    Optional<DBTheme> themeOptional = themeRepository.findById(
                        themeId
                    );
                    return themeOptional
                        .map(theme -> modelMapper.map(theme, GetThemeDTO.class))
                        .orElse(null);
                })
                .filter(themeDTO -> themeDTO != null)
                .collect(Collectors.toList());

            userDTO.setSubscriptions(themeDTOs);
        } else {
            userDTO.setSubscriptions(new ArrayList<>());
        }
        return userDTO;
    }

    public GetUserDTO subscribeUserToTheme(String themeId, String userEmail) {
        DBUser user = userRepository
            .findByEmail(userEmail).orElseThrow(
                () -> new NoSuchElementException(
                    "User not found with email : " + userEmail
                ));
        DBTheme theme = themeRepository
            .findById(themeId)
            .orElseThrow(() ->
                new NoSuchElementException(
                    "theme not found with ID : " + themeId
                )
            );
        List<String> themeExisting = user.getSubscriptions();

        if (themeExisting == null) {
            themeExisting = new ArrayList<>();
            user.setSubscriptions(themeExisting);
        }

        if (!themeExisting.contains(theme.getId())) {
            themeExisting.add(theme.getId());
            userRepository.save(user);
        }

        return getUserByEmail(userEmail);
    }

    public GetUserDTO unsuscribeUserToTheme(String themeId, String userEmail) {
        DBUser user = userRepository
            .findByEmail(userEmail)
            .orElseThrow(() ->
                new NoSuchElementException("User not found with ID : " + userEmail)
            );

        List<String> subscriptions = user.getSubscriptions();
        if (subscriptions != null) {
            boolean removed = subscriptions.remove(themeId);
            if (removed) {
                userRepository.save(user);
            }
        }
        return getUserByEmail(userEmail);
    }

    public void deleteUser(String userId) {
        userRepository
            .findById(userId)
            .orElseThrow(() ->
                new NoSuchElementException("User not found with ID : " + userId)
            );

        userRepository.deleteById(userId);
    }
}
