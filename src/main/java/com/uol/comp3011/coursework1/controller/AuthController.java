package com.uol.comp3011.coursework1.controller;

import com.uol.comp3011.coursework1.dto.request.UserRequest;
import com.uol.comp3011.coursework1.dto.response.MessageResponse;
import com.uol.comp3011.coursework1.service.AuthService;
import com.uol.comp3011.coursework1.dto.response.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/create-account")
  public ResponseEntity<MessageResponse> createAccount(@RequestBody UserRequest newUser) {
    if (newUser.email().isBlank() || newUser.password().isBlank()) {
      // Validate request
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Email or Password Provided");
    }

    try {
      authService.createAccount(newUser);
      return ResponseEntity.ok().body(new MessageResponse("Account Created Successfully"));
    } catch (Exception error) {
      // Account Creation Failed
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody UserRequest newUser) {
    if (newUser.email().isBlank() || newUser.password().isBlank()) {
      // Validate request
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Email or Password Provided");
    }

    try {
      String token = authService.login(newUser);
      return ResponseEntity.ok().body(new LoginResponse(token));
    } catch (Exception error) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }
}
