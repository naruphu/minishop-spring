package com.minishop.minishopspring.service;

import com.minishop.minishopspring.dto.LoginResponseDTO;
import com.minishop.minishopspring.dto.UserLoginRequestDTO;
import com.minishop.minishopspring.dto.UserRegisterRequestDTO;
import com.minishop.minishopspring.dto.UserResponseDTO;
import com.minishop.minishopspring.entity.User;
import com.minishop.minishopspring.enums.Role;
import com.minishop.minishopspring.exception.InvalidCredentialsException;
import com.minishop.minishopspring.exception.UserAlreadyExistsException;
import com.minishop.minishopspring.exception.UserNotFoundException;
import com.minishop.minishopspring.mapper.UserMapper;
import com.minishop.minishopspring.repository.UserRepository;
import com.minishop.minishopspring.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    // GET ALL USERS
    public List<UserResponseDTO> getAllUsers() {

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toDTO)
                .toList();
    }


    // GET USER BY ID
    public UserResponseDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + id)
                );

        return userMapper.toDTO(user);
    }


    // REGISTER
    public UserResponseDTO register(UserRegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already existed");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already existed");
        }

        User user = userMapper.toEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Người đăng ký mặc định là CUSTOMER
        user.setRole(Role.CUSTOMER);

        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    public void delete(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
        System.out.println("Delete successfully!");
    }

    public LoginResponseDTO login(UserLoginRequestDTO request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password!") );

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid username or password!");
        }

        String token = jwtService.generateToken(user.getUsername());
        return new LoginResponseDTO(token, user.getUsername(), user.getRole());

    }


}