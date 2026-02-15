package com.uol.comp3011.Coursework1.Controller;

import com.uol.comp3011.Coursework1.Service.AuthService;
import com.uol.comp3011.Coursework1.Structs.LoginResponse;
import com.uol.comp3011.Coursework1.Structs.NewUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/createAccount")
  public ResponseEntity<?> createAccount(@RequestBody NewUser newUser) {
    try {
      authService.createAccount(newUser);
      return ResponseEntity.ok().body("USER CREATED SUCCESSFULLY");
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("USER CREATION FAILED");
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody NewUser newUser) {
    try {
      String token = authService.login(newUser);
      return ResponseEntity.ok().body(new LoginResponse(token));
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("LOGIN FAILED");
    }
  }
}
