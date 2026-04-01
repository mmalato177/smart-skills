package com.smart_skills.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.smart_skills.dto.ChangePassword;
import com.smart_skills.dto.LoginDTO;
import com.smart_skills.models.User;
import com.smart_skills.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Path uploadDir = Paths.get("uploads/profile");

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request, HttpSession session) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciais inválidas"));
        }

        User user = userOpt.get();

        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Utilizador inativo"));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciais inválidas"));
        }

        // CRUCIAL — autenticar no Spring Security
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                java.util.List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        // os teus atributos (opcional)
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        return ResponseEntity.ok(Map.of(
                "message", "Login com sucesso"
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpSession session) {
        Object userIdObj = session.getAttribute("userId");

        if (userIdObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Não autenticado"));
        }

        Long userId = ((Number) userIdObj).longValue();
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Utilizador não encontrado"));
        }

        User user = userOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        response.put("profileImageUrl", user.getProfileImageUrl());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logout com sucesso"));
    }

    @PostMapping("/profile-image")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("image") MultipartFile image, HttpSession session) {
        Object userIdObj = session.getAttribute("userId");

        if (userIdObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Não autenticado"));
        }

        if (image.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Nenhuma imagem enviada"));
        }

        String contentType = image.getContentType();
        if (contentType == null
                || (!contentType.equals("image/jpeg")
                && !contentType.equals("image/png")
                && !contentType.equals("image/webp"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Formato inválido. Usa JPG, PNG ou WEBP."));
        }

        if (image.getSize() > 10 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "A imagem não pode exceder 10MB."));
        }

        Long userId = ((Number) userIdObj).longValue();
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Utilizador não encontrado"));
        }

        User user = userOpt.get();

        try {
            Files.createDirectories(uploadDir);

            String originalFilename = StringUtils.cleanPath(image.getOriginalFilename());
            String extension = "";

            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalFilename.substring(dotIndex);
            }

            String newFilename = UUID.randomUUID() + extension;
            Path destination = uploadDir.resolve(newFilename);

            Files.copy(image.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            user.setProfileImageUrl("/uploads/profile/" + newFilename);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Imagem atualizada com sucesso",
                    "profileImageUrl", user.getProfileImageUrl()
            ));

        } catch (IOException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erro ao guardar imagem"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword request, HttpSession session) {
        Object userIdObj = session.getAttribute("userId");

        if (userIdObj == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Não autenticado"));
        }

        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()
                || request.getNewPassword() == null || request.getNewPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Todos os campos são obrigatórios"));
        }

        if (request.getNewPassword().length() < 6) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "A nova password deve ter pelo menos 6 caracteres"));
        }

        Long userId = ((Number) userIdObj).longValue();
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Utilizador não encontrado"));
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "A password atual está incorreta"));
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password alterada com sucesso"));
    }
}
