package com.uol.comp3011.coursework1.dal.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "app_user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column private String email;

  @Column(name = "password_hash")
  private String passwordHash;

  /* There is no way to tell JPA that we are automatically generating a timestamp, so to workaround we just tell
   JPA that the column is not insertable. This will stop it from throwing errors when we provide a row without
   a created_at column.
  */
  @Column(name = "created_at", insertable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  // Many-to-many relationship between user and roles - join table required.
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "app_user_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<AppRole> roles = new HashSet<>();

  // For debugging purposes.
  @Override
  public String toString() {
    var roleNames = roles.stream().map(AppRole::getName).toList();
    return String.format("id:%d, email:%s, roles:%s", id, email, roleNames);
  }
}
