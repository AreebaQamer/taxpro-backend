package com.taxpro.controller;

import com.taxpro.entity.User;
import com.taxpro.repository.UserRepository;
import com.taxpro.service.PasswordChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")  // ✅ wapas add karo

public class AuthController {

    @Autowired
    private UserRepository userRepository;

   @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
    String username = body.get("username");
    String password = body.get("password");

    System.out.println("=== LOGIN DEBUG ===");
    System.out.println("Username entered: " + username);

    Optional<User> userOpt = userRepository.findByUserLogin(username.trim());

    if (userOpt.isEmpty()) {
        System.out.println("❌ User not found in DB");
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    User user = userOpt.get();
    System.out.println("✅ User found: " + user.getUserLogin());
    System.out.println("DB hash: " + user.getUserPass());

    String fixedHash = user.getUserPass()
        .replace("$wp$2y$", "$2a$")
        .replace("$2y$", "$2a$");
    System.out.println("Fixed hash: " + fixedHash);

    PasswordChecker checker = new PasswordChecker();
    boolean match = checker.checkPassword(password, user.getUserPass());
    System.out.println("Password match: " + match);
    System.out.println("==================");

    if (!match) {
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    return ResponseEntity.ok(Map.of(
        "token",       "admin-token-" + user.getId() + "-" + System.currentTimeMillis(),
        "username",    user.getUserLogin(),
        "displayName", user.getDisplayName() != null ? user.getDisplayName() : username,
        "message",     "Login successful"
    ));
}
   private boolean checkWordPressPassword(String plainPassword, String hashedPassword) {
    PasswordChecker checker = new PasswordChecker();
    return checker.checkPassword(plainPassword, hashedPassword);
}
// AuthController me temporarily add karo
@GetMapping("/generate-hash")
public ResponseEntity<?> generateHash(@RequestParam String pass) {
    BCryptPasswordEncoder b = new BCryptPasswordEncoder();
    String hash = "$wp$" + b.encode(pass);
    System.out.println("Generated: " + hash);
    return ResponseEntity.ok(Map.of("hash", hash));
}

@GetMapping("/verify")
public ResponseEntity<?> verifyToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return ResponseEntity.status(401).body(Map.of("error", "No token"));
    }
    String token = authHeader.substring(7);
    // Simple check — token exist karta hai aur format sahi hai
    if (token.startsWith("admin-token-")) {
        return ResponseEntity.ok(Map.of("valid", true));
    }
    return ResponseEntity.status(401).body(Map.of("error", "Invalid token"));
}
}