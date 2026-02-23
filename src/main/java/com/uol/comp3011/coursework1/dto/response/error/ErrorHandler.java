package com.uol.comp3011.coursework1.dto.response.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

  /**
   * This function defines the response that a REST controller should send upon encountering a
   * ResponseStatusException.
   *
   * @param e A ResponseStatusException thrown by a REST controller.
   * @return An HTTP response including the relevant status code and a JSON body including the error
   *     message.
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatusException(
      ResponseStatusException e) {
    Map<String, Object> response = new HashMap<>();
    response.put("Message", "Error:  " + e.getReason());
    response.put("Error Code", e.getStatusCode().value());

    return ResponseEntity.status(e.getStatusCode()).body(response);
  }
}
