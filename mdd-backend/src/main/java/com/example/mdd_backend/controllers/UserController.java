package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.CreateUserDTO;
import com.example.mdd_backend.dtos.GetUserDTO;
import com.example.mdd_backend.dtos.UpdateUserDTO;
import com.example.mdd_backend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetUserDTO> getUserById(@PathVariable String id) {
        GetUserDTO user = userService.getUserById(id);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<GetUserDTO>> getUsers() {
        List<GetUserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<GetUserDTO> createUser(
            @Valid @RequestBody CreateUserDTO userDTO
    ) {
        GetUserDTO createdUser = userService.createUser(userDTO);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable String id) {
        userService.deleteUser(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{themeId}/subscriptions")
    public ResponseEntity<GetUserDTO> subscribeToTheme(@PathVariable String themeId, Authentication authentication, HttpServletRequest request) {
        String userId = authentication.getName();
        GetUserDTO user = userService.subscribeUserToTheme(themeId, userId);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @DeleteMapping("/{themeId}/unsubscriptions")
    public ResponseEntity<GetUserDTO> unsubscribeToTheme(@PathVariable String themeId, Authentication authentication) {
        String userId = authentication.getName();
        GetUserDTO user = userService.unsuscribeUserToTheme(themeId, userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GetUserDTO> updateUser(@PathVariable String id, @RequestBody UpdateUserDTO updateUserDTO) {
        GetUserDTO updatedUser = userService.updateUser(id, updateUserDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

}
