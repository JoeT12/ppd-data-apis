package com.uol.comp3011.Coursework1.Structs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
  private final String bearerToken;
}
