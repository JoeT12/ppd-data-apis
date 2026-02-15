package com.uol.comp3011.Coursework1.Repository;

import com.uol.comp3011.Coursework1.Entity.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<AppRole, Long> {
    AppRole findByName(String name);
}
