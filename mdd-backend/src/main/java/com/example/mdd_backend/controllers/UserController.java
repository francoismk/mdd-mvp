package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.AuthResponseDTO;
import com.example.mdd_backend.dtos.UserCreateRequestDTO;
import com.example.mdd_backend.dtos.UserResponseDTO;
import com.example.mdd_backend.dtos.UserUpdateRequestDTO;
import com.example.mdd_backend.services.AuthService;
import com.example.mdd_backend.services.JWTService;
import com.example.mdd_backend.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@Tag(name = "Users", description = "User management operations")
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final JWTService jwtService;

    public UserController(UserService userService, AuthService authService, JWTService jwtService) {
        this.userService = userService;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return ResponseEntity containing the UserResponseDTO if found,
     *         or an HTTP 404 Not Found status if the user does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
        @PathVariable String id
    ) {
        UserResponseDTO user = userService.getUserById(id);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Retrieves all users.
     *
     * @return ResponseEntity containing a list of UserResponseDTO if users exist,
     *         or an empty list if no users are found.
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Retrieves the current user's information.
     *
     * @param authentication the authentication object representing the user's authentication status
     * @return ResponseEntity containing the UserResponseDTO if the user is authenticated,
     *         or an HTTP 404 Not Found status if the user is not found.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(
        Authentication authentication
    ) {
        String userEmail = authentication.getName();
        UserResponseDTO user = userService.getUserByEmail(userEmail);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Creates a new user.
     *
     * @param userDTO The DTO containing the user's information. Must be a valid object.
     * @return ResponseEntity containing the created UserResponseDTO if successful,
     *         or an HTTP 201 Created status.
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
        @Valid @RequestBody UserCreateRequestDTO userDTO
    ) {
        UserResponseDTO createdUser = userService.createUser(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @return ResponseEntity with an HTTP 200 OK status upon successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable String id) {
        userService.deleteUser(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Subscribes the current user to a theme.
     *
     * @param themeId The ID of the theme to subscribe to.
     * @param authentication The authentication object representing the user.
     * @return ResponseEntity containing the updated UserResponseDTO if successful,
     *         or an HTTP 201 Created status.
     */
    @PostMapping("/{themeId}/subscriptions")
    public ResponseEntity<UserResponseDTO> subscribeToTheme(
        @PathVariable String themeId,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        UserResponseDTO user = userService.subscribeUserToTheme(
            themeId,
            userId
        );
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    /**
     * Unsubscribes the current user from a theme.
     *
     * @param themeId The ID of the theme to unsubscribe from.
     * @param authentication The authentication object representing the user.
     * @return ResponseEntity containing the updated UserResponseDTO if successful,
     *         or an HTTP 200 OK status.
     */
    @DeleteMapping("/{themeId}/unsubscriptions")
    public ResponseEntity<UserResponseDTO> unsubscribeToTheme(
        @PathVariable String themeId,
        Authentication authentication
    ) {
        String userId = authentication.getName();
        UserResponseDTO user = userService.unsuscribeUserToTheme(
            themeId,
            userId
        );
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Updates a user by their ID.
     *
     * @param id The ID of the user to update.
     * @param updateUserDTO The DTO containing the user's updated information.
     * @return ResponseEntity containing the updated UserResponseDTO if successful,
     *         or an HTTP 200 OK status.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
        @PathVariable String id,
        @RequestBody UserUpdateRequestDTO updateUserDTO
    ) {
        UserResponseDTO updatedUser = userService.updateUser(id, updateUserDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Updates the current user's information.
     *
     * @param updateUserDTO The DTO containing the user's updated information.
     * @param authentication The authentication object representing the user.
     * @param response The HTTP servlet response for updating the auth cookie.
     * @return ResponseEntity containing the updated UserResponseDTO if successful,
     *         or an HTTP 200 OK status.
     */
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateCurrentUser(
        @RequestBody UserUpdateRequestDTO updateUserDTO,
        Authentication authentication,
        HttpServletResponse response
    ) {
        String currentUserEmail = authentication.getName();
        UserResponseDTO updatedUser = userService.updateUserByEmail(currentUserEmail, updateUserDTO);
        
        String newUserIdentifier = updateUserDTO.getEmail() != null ? updateUserDTO.getEmail() : currentUserEmail;
        AuthResponseDTO newToken = jwtService.getTokenFromUserIdentifier(newUserIdentifier);
        authService.addAuthCookie(newToken, response);
        
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

}
