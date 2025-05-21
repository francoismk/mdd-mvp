package com.example.mdd_backend.controllers;

import com.example.mdd_backend.dtos.CreateUserDTO;
import com.example.mdd_backend.dtos.GetUserDTO;
import com.example.mdd_backend.services.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

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
    public ResponseEntity<GetUserDTO> subscribeToTheme(@PathVariable String themeId, @RequestParam String userId) {
        GetUserDTO user = userService.subscribeUserToTheme(themeId, userId);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @DeleteMapping("/{themeId}/unsubscriptions")
    public ResponseEntity<GetUserDTO> unsubscribeToTheme(@PathVariable String themeId, @RequestParam String userId) {
        GetUserDTO user = userService.unsuscribeUserToTheme(themeId, userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
