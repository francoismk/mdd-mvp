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
import java.util.Optional;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for managing users and their subscriptions.
 *
 * Handles user creation, updates, topic subscriptions and retrieval operations.
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

    /**
     * Creates a new user account.
     *
     * @param userDTO User registration data
     * @return Created user with generated ID
     * @throws DuplicateResourceException If email or username already exists
     * @throws RuntimeException On creation failure
     */
    public UserResponseDTO createUser(UserCreateRequestDTO userDTO) {
        try {
            if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
                logger.warn(
                    "User creation failed: Email already exists: {}",
                    userDTO.getEmail()
                );
                throw new DuplicateResourceException(
                    "Cet email existe déjà"
                );
            }
            
            if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
                logger.warn(
                    "User creation failed: Username already exists: {}",
                    userDTO.getUsername()
                );
                throw new DuplicateResourceException(
                    "Ce nom d'utilisateur existe déjà"
                );
            }
            
            DBUser user = modelMapper.map(userDTO, DBUser.class);
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            DBUser savedUser = userRepository.save(user);
            return modelMapper.map(savedUser, UserResponseDTO.class);
        } catch (DuplicateResourceException e) {
            throw e;
        } catch (DuplicateKeyException e) {
            logger.warn(
                "User creation failed: Email or username already exists: {}",
                userDTO.getEmail()
            );
            throw new DuplicateResourceException(
                "Cet email ou ce nom d'utilisateur existe déjà"
            );
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user");
        }
    }

    /**
     * Retrieves all users in the system.
     *
     * @return List of all users with their subscriptions
     * @throws DatabaseOperationException On retrieval failure
     */
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

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id The unique identifier of the user
     * @return User with subscription details
     * @throws ResourceNotFoundException If user doesn't exist
     * @throws DatabaseOperationException On retrieval failure
     */
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

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email address of the user
     * @return User with subscription details
     * @throws ResourceNotFoundException If user doesn't exist
     * @throws DatabaseOperationException On retrieval failure
     */
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

    /**
     * Subscribes a user to a topic.
     *
     * @param themeId The unique identifier of the topic
     * @param userEmail The email of the user to subscribe
     * @return Updated user with new subscription
     * @throws ResourceNotFoundException If user or topic doesn't exist
     * @throws BusinessLogicException On subscription failure
     */
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

    /**
     * Unsubscribes a user from a topic.
     *
     * @param themeId The unique identifier of the topic
     * @param userEmail The email of the user to unsubscribe
     * @return Updated user without the subscription
     * @throws ResourceNotFoundException If user doesn't exist
     * @throws BusinessLogicException On unsubscription failure
     */
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

    /**
     * Deletes a user by ID.
     *
     * @param userId The unique identifier of the user to delete
     * @throws ResourceNotFoundException If user doesn't exist
     * @throws DatabaseOperationException On deletion failure
     */
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

    /**
     * Updates user information.
     *
     * Validates email uniqueness before updating.
     *
     * @param userId The unique identifier of the user to update
     * @param updateUserDTO Updated user data
     * @return Updated user information
     * @throws ResourceNotFoundException If user doesn't exist
     * @throws DuplicateResourceException If new email already exists
     * @throws DatabaseOperationException On update failure
     */
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
                Optional<DBUser> existingUserWithUsername = userRepository.findByUsername(updateUserDTO.getUsername());
                if (existingUserWithUsername.isPresent() && !existingUserWithUsername.get().getId().equals(user.getId())) {
                    throw new DuplicateResourceException(
                            "Ce nom d'utilisateur existe déjà"
                    );
                }
                user.setUsername(updateUserDTO.getUsername());
            }

            if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
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

    /**
     * Updates user information.
     *
     * Validates email and username uniqueness before updating.
     *
     * @param userEmail email of the user to update
     * @param updateUserDTO Updated user data
     * @return Updated user information
     * @throws ResourceNotFoundException If user doesn't exist
     * @throws DuplicateResourceException If new email or username already exists
     * @throws DatabaseOperationException On update failure
     */
    public UserResponseDTO updateUserByEmail(
            String userEmail,
            UserUpdateRequestDTO updateUserDTO
    ) {
        try {
            DBUser user = userRepository
                    .findByEmail(userEmail)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "User not found with email: " + userEmail
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
                Optional<DBUser> existingUserWithUsername = userRepository.findByUsername(updateUserDTO.getUsername());
                if (existingUserWithUsername.isPresent() && !existingUserWithUsername.get().getId().equals(user.getId())) {
                    throw new DuplicateResourceException(
                            "Ce nom d'utilisateur existe déjà"
                    );
                }
                user.setUsername(updateUserDTO.getUsername());
            }

            if (updateUserDTO.getPassword() != null && 
                !updateUserDTO.getPassword().trim().isEmpty()) {
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

    /**
     * Builds user response DTO with subscription details.
     *
     * @param user Database user entity
     * @return User response DTO with enriched subscription data
     * @throws BusinessLogicException On mapping failure
     */
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

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
