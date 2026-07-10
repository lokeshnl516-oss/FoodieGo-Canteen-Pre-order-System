package com.foodiego.canteen.controller;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodiego.canteen.dto.ApiResponse;
import com.foodiego.canteen.dto.AuthResponse;
import com.foodiego.canteen.dto.LoginRequest;
import com.foodiego.canteen.dto.RegisterRequest;
import com.foodiego.canteen.dto.UserResponse;
import com.foodiego.canteen.entity.Admin;
import com.foodiego.canteen.entity.User;
import com.foodiego.canteen.security.JwtUtil;
import com.foodiego.canteen.service.AdminService;
import com.foodiego.canteen.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setPassword(request.getPassword());
            
            User savedUser = userService.registerUser(user);
            UserResponse response = new UserResponse(
                savedUser.getUserId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginUser(@Valid @RequestBody LoginRequest request) {
        Optional<User> user = userService.loginUser(request.getEmail(), request.getPassword());
        if (user.isPresent()) {
            String token = jwtUtil.generateToken(user.get().getEmail(), "USER");
            AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                user.get().getUserId(),
                user.get().getEmail(),
                "USER"
            );
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(401, "Invalid email or password"));
    }

    @PostMapping("/admin-login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginAdmin(@Valid @RequestBody LoginRequest request) {
        Optional<Admin> admin = adminService.loginAdmin(request.getEmail(), request.getPassword());
        if (admin.isPresent()) {
            String token = jwtUtil.generateToken(admin.get().getEmail(), "ADMIN");
            AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                admin.get().getAdminId(),
                admin.get().getEmail(),
                "ADMIN"
            );
            return ResponseEntity.ok(ApiResponse.success("Admin login successful", response));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(401, "Invalid email or password"));
    }
}