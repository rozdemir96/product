package com.project.product.controller.login.service;

import com.project.product.controller.login.model.LoginRequestModel;
import com.project.product.controller.login.model.LoginResponseModel;
import com.project.product.controller.login.model.UserInfoResponseModel;
import com.project.product.entity.User;
import com.project.product.repository.UserRepository;
import com.project.product.security.AuthenticationFacade;
import com.project.product.security.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Kimlik Doğrulama", description = "Kullanıcı giriş ve kimlik doğrulama işlemleri yönetilir.")
public class LoginController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationFacade authenticationFacade;

    public LoginController(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationFacade authenticationFacade) {
        this.userRepository = userRepository;
        this.authenticationFacade = authenticationFacade;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtUtil = jwtUtil;
    }

    /**
     * Kullanıcı giriş işlemi
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestModel loginRequestModel) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequestModel.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(loginRequestModel.getPassword(), user.getPassword())) {
                String accessToken = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
                return ResponseEntity.ok(new LoginResponseModel(accessToken));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }

    /**
     * Kimlik doğrulaması yapılmış kullanıcı bilgilerini döndürür
     */
    @GetMapping("/getCurrentUser")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserInfoResponseModel> getCurrentUser() {
        User currentUser = authenticationFacade.getCurrentUser();
        return ResponseEntity.ok(new UserInfoResponseModel(currentUser.getId(), currentUser.getUsername(), currentUser.getRole()));
    }
}
