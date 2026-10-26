package com.minishop.minishopspring.controller;

import com.minishop.minishopspring.dto.LoginResponseDTO;
import com.minishop.minishopspring.dto.UserLoginRequestDTO;
import com.minishop.minishopspring.dto.UserRegisterRequestDTO;
import com.minishop.minishopspring.dto.UserResponseDTO;
import com.minishop.minishopspring.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @Valid @RequestBody UserRegisterRequestDTO request
    ) {
        UserResponseDTO response = userService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.noContent().build();

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody UserLoginRequestDTO request
    ) {

        LoginResponseDTO response = userService.login(request);
        return ResponseEntity.ok(response);

    }
}