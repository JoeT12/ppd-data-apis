package com.uol.comp3011.coursework1.service;

import com.uol.comp3011.coursework1.dal.entity.User;
import com.uol.comp3011.coursework1.dal.repository.RoleRepository;
import com.uol.comp3011.coursework1.dal.repository.UserRepository;
import com.uol.comp3011.coursework1.config.security.JWTUtils;
import com.uol.comp3011.coursework1.dto.request.UserRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
public class AuthService {
  private static final Logger log = LoggerFactory.getLogger(AuthService.class);

  private final UserRepository userRepo;
  private final RoleRepository roleRepo;
  private final AuthenticationManager authenticationManager;
  private final BCryptPasswordEncoder encoder;
  private final JWTUtils jwtUtil;

  public AuthService(
      UserRepository userRepo,
      RoleRepository roleRepo,
      AuthenticationManager authenticationManager,
      BCryptPasswordEncoder encoder,
      JWTUtils jwtUtil) {
    this.userRepo = userRepo;
    this.roleRepo = roleRepo;
    this.authenticationManager = authenticationManager;
    this.encoder = encoder;
    this.jwtUtil = jwtUtil;
  }

  @Transactional
  public void createAccount(UserRequest user) {
    log.info("Beginning Account Creation for User {}", user.email());
    User newUser = new User();
    newUser.setEmail(user.email());
    newUser.setPasswordHash(encoder.encode(user.password()));
    newUser.setRoles(Set.of(roleRepo.findByName("ROLE_USER")));
    newUser.setUpdatedAt(Instant.now());
    userRepo.save(newUser);
    log.info("Completed Account Creation for User {}", user.email());
  }

  @Transactional
  public String login(UserRequest user) {
    // Most code in this method was taken from the suggestion by ChatGPT when creating the Security
    // package.
    // Evidence of the original code provided in the response from ChatGPT can be found in the
    // AI_ACKNOWLEDGMENT.md in the security module.
    log.info("Beginning User Login for User {}", user.email());
    Authentication auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.email(), user.password()));
    UserDetails userDetails = (UserDetails) auth.getPrincipal();
    if (userDetails == null) {
      throw new BadCredentialsException(null);
    }
    String token = jwtUtil.generateToken(userDetails);
    log.info("User Login Complete for User {}", user.email());
    return token;
  }
}
