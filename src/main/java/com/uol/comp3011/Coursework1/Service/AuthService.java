package com.uol.comp3011.Coursework1.Service;

import com.uol.comp3011.Coursework1.Repository.RoleRepository;
import com.uol.comp3011.Coursework1.Repository.UserRepository;
import com.uol.comp3011.Coursework1.Security.JWTUtils;
import com.uol.comp3011.Coursework1.Structs.NewUser;
import com.uol.comp3011.Coursework1.Entity.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
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
  public void createAccount(NewUser user) {
    log.info("AuthService:createAccount:: INFO - BEGIN");
    User newUser = new User();
    newUser.setEmail(user.getEmail());
    newUser.setPasswordHash(encoder.encode(user.getPassword()));
    newUser.setRoles(Set.of(roleRepo.findByName("ROLE_USER")));
    newUser.setUpdatedAt(Instant.now());
    userRepo.save(newUser);
    log.info("AuthService:createAccount:: INFO - END");
  }

  @Transactional
  public String login(NewUser user) throws Exception {
    log.info("AuthService:login:: INFO - BEGIN");
    Authentication auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
    UserDetails nuser = (UserDetails) auth.getPrincipal();
    String token = jwtUtil.generateToken(nuser);
    log.info("AuthService:login:: INFO - END");
    return token;
  }
}
