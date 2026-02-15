package com.uol.comp3011.Coursework1.Repository;

import com.uol.comp3011.Coursework1.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
