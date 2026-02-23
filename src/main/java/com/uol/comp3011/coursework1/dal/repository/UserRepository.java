package com.uol.comp3011.coursework1.dal.repository;

import com.uol.comp3011.coursework1.dal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  User findByEmail(String email);
}
