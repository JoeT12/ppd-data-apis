package com.uol.comp3011.coursework1.dal.repository;

import com.uol.comp3011.coursework1.dal.entity.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<AppRole, Long> {
  AppRole findByName(String name);
}
