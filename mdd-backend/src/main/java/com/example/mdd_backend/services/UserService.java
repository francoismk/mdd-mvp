package com.example.mdd_backend.services;

import com.example.mdd_backend.dtos.TopicResponseDTO;
import com.example.mdd_backend.dtos.UserCreateRequestDTO;
import com.example.mdd_backend.dtos.UserResponseDTO;
import com.example.mdd_backend.dtos.UserUpdateRequestDTO;
import com.example.mdd_backend.errors.exceptions.BusinessLogicException;
import com.example.mdd_backend.errors.exceptions.DatabaseOperationException;
import com.example.mdd_backend.errors.exceptions.DuplicateResourceException;
import com.example.mdd_backend.errors.exceptions.ResourceNotFoundException;
import com.example.mdd_backend.models.DBTopic;
import com.example.mdd_backend.models.DBUser;
import com.example.mdd_backend.repositories.TopicRepository;
import com.example.mdd_backend.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * The type User service.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(
        UserService.class
    );

    private final UserRepository userRepository;
    private final TopicRepository themeRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserService(
        UserRepository userRepository,
        TopicRepository themeRepository,
        PasswordEncoder passwordEncoder,
        ModelMapper modelMapper
    ) {
        this.userRepository = userRepository;
        this.themeRepository = themeRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public UserResponseDTO createUser(UserCreateRequestDTO userDTO) {
        try {
            DBUser user = modelMapper.map(userDTO, DBUser.class);
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            DBUser savedUser = userRepository.save(user);
            return modelMapper.map(savedUser, UserResponseDTO.class);
        } catch (DuplicateKeyException e) {
            logger.warn(
                "User creation failed: Email or username already exists: {}",
                userDTO.getEmail()
            );
            throw new DuplicateResourceException(
                "User with this email or username already exists"
            );
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user");
        }
    }

    public List<UserResponseDTO> getAllUsers() {
        try {
            List<DBUser> users = userRepository.findAll();
            return users
                .stream()
                .map(user -> {
                    return buildUserDto(user);
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error retrieving all users: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to retrieve users");
        }
    }

    public UserResponseDTO getUserById(String id) {
        try {
            DBUser user = userRepository
                .findById(id)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "user not found with ID : " + id
                    )
                );

            return buildUserDto(user);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error(
                "Error retrieving user with ID {}: {}",
                id,
                e.getMessage(),
                e
            );
            throw new DatabaseOperationException("Failed to retrieve user");
        }
    }

    public UserResponseDTO getUserByEmail(String email) {
        try {
            DBUser user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "user not found with email : " + email
                    )
                );

            return buildUserDto(user);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error(
                "Error retrieving user with email {}: {}",
                email,
                e.getMessage(),
                e
            );
            throw new DatabaseOperationException("Failed to retrieve user");
        }
    }

    private UserResponseDTO buildUserDto(DBUser user) {
        try {
            UserResponseDTO userDTO = modelMapper.map(
                user,
                UserResponseDTO.class
            );

            if (
                user.getSubscribedTopicIds() != null &&
                !user.getSubscribedTopicIds().isEmpty()
            ) {
                List<TopicResponseDTO> themeDTOs = user
                    .getSubscribedTopicIds()
                    .stream()
                    .map(themeId -> {
                        try {
                            Optional<DBTopic> themeOptional =
                                themeRepository.findById(themeId);
                            return themeOptional
                                .map(theme ->
                                    modelMapper.map(
                                        theme,
                                        TopicResponseDTO.class
                                    )
                                )
                                .orElse(null);
                        } catch (Exception e) {
                            logger.warn(
                                "Error retrieving topic with ID {}: {}",
                                themeId,
                                e.getMessage()
                            );
                            return null;
                        }
                    })
                    .filter(themeDTO -> themeDTO != null)
                    .collect(Collectors.toList());

                userDTO.setSubscriptions(themeDTOs);
            } else {
                userDTO.setSubscriptions(new ArrayList<>());
            }
            return userDTO;
        } catch (Exception e) {
            logger.error("Error building user DTO: {}", e.getMessage(), e);
            throw new BusinessLogicException("Failed to map user data");
        }
    }

    public UserResponseDTO subscribeUserToTheme(
        String themeId,
        String userEmail
    ) {
        try {
            DBUser user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found with email : " + userEmail
                    )
                );
            DBTopic theme = themeRepository
                .findById(themeId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "theme not found with ID : " + themeId
                    )
                );
            List<String> themeExisting = user.getSubscribedTopicIds();

            if (themeExisting == null) {
                themeExisting = new ArrayList<>();
                user.setSubscribedTopicIds(themeExisting);
            }

            if (!themeExisting.contains(theme.getId())) {
                themeExisting.add(theme.getId());
                userRepository.save(user);
            }

            return getUserByEmail(userEmail);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error(
                "Error subscribing user to theme: {}",
                e.getMessage(),
                e
            );
            throw new BusinessLogicException(
                "Failed to subscribe user to theme"
            );
        }
    }

    public UserResponseDTO unsuscribeUserToTheme(
        String themeId,
        String userEmail
    ) {
        try {
            DBUser user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found with ID : " + userEmail
                    )
                );

            List<String> subscriptions = user.getSubscribedTopicIds();
            if (subscriptions != null) {
                boolean removed = subscriptions.remove(themeId);
                if (removed) {
                    userRepository.save(user);
                }
            }
            return getUserByEmail(userEmail);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error(
                "Error unsubscribing user from theme: {}",
                e.getMessage(),
                e
            );
            throw new BusinessLogicException(
                "Failed to unsubscribe user from theme"
            );
        }
    }

    public void deleteUser(String userId) {
        try {
            userRepository
                .findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found with ID : " + userId
                    )
                );

            userRepository.deleteById(userId);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error(
                "Error deleting user with ID {}: {}",
                userId,
                e.getMessage(),
                e
            );
            throw new DatabaseOperationException("Failed to delete user");
        }
    }

    public UserResponseDTO updateUser(
        String userId,
        UserUpdateRequestDTO updateUserDTO
    ) {
        try {
            DBUser user = userRepository
                .findById(userId)
                .orElseThrow(() ->
                    new ResourceNotFoundException(
                        "User not found with ID: " + userId
                    )
                );

            if (
                updateUserDTO.getEmail() != null &&
                !updateUserDTO.getEmail().equals(user.getEmail())
            ) {
                if (
                    userRepository
                        .findByEmail(updateUserDTO.getEmail())
                        .isPresent()
                ) {
                    throw new DuplicateResourceException(
                        "Email already exists"
                    );
                }
                user.setEmail(updateUserDTO.getEmail());
            }

            if (updateUserDTO.getUsername() != null) {
                user.setUsername(updateUserDTO.getUsername());
            }

            if (updateUserDTO.getPassword() != null) {
                user.setPassword(
                    passwordEncoder.encode(updateUserDTO.getPassword())
                );
            }

            DBUser savedUser = userRepository.save(user);
            return buildUserDto(savedUser);
        } catch (ResourceNotFoundException | DuplicateResourceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while update user: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Failed to update user");
        }
    }
}
