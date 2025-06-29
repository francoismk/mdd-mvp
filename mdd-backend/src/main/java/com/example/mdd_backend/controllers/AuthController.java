package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.AuthResponseDTO;
import com.example.mdd_backend.dtos.LoginRequestDTO;
import com.example.mdd_backend.dtos.UserCreateRequestDTO;
import com.example.mdd_backend.dtos.UserResponseDTO;
import com.example.mdd_backend.services.AuthService;
import com.example.mdd_backend.services.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(
    name = "Authentication",
    description = "Authentication management operations"
)
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    /**
     * Creates a new user.
     *
     * @param userDTO  The DTO containing the user's information.
     * @param response The HTTP servlet response.
     * @return ResponseEntity containing the authentication token upon successful registration,
     *         or an appropriate error status if registration fails.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> createUser(
        @Valid @RequestBody UserCreateRequestDTO userDTO, HttpServletResponse response
    ) {
        AuthResponseDTO token = authService.registerAndGenerateToken(userDTO);
        authService.addAuthCookie(token, response);
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    /**
     * Logs in an existing user.
     *
     * @param loginRequestDTO The DTO containing the user's login credentials.
     * @param response        The HTTP servlet response.
     * @return ResponseEntity containing the authentication token upon successful login,
     *         or an appropriate error status if login fails.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(
        @Valid @RequestBody LoginRequestDTO LoginRequestDTO, HttpServletResponse response
    ) {
        AuthResponseDTO token = authService.authenticateAndGenerateToken(
            LoginRequestDTO
        );
        authService.addAuthCookie(token, response);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    /**
     * Logs out the current user.
     *
     * @param response The HTTP servlet response.
     * @return ResponseEntity with an HTTP 200 OK status upon successful logout.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(HttpServletResponse response) {
        authService.removeAuthCookie(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Retrieves the current user's information.
     *
     * @param authentication the authentication object representing the user's authentication status
     * @return ResponseEntity containing the UserResponseDTO if the user is authenticated,
     *         or an HTTP 401 Unauthorized status if not authenticated.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    UserResponseDTO userDTO = userService.getUserByEmail(authentication.getName());
    return ResponseEntity.ok(userDTO);
}
}
