package com.uol.comp3011.coursework1.dto.response.error;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

  // Injected from application.properties file.
  @Value("${app.development.environment}")
  public boolean devEnvironment;

  public static final String INTERNAL_SERVER_ERROR_MESSAGE =
      "An unexpected server error occurred - please contact the system administrator";

  /**
   * This function defines the response that a REST controller should send upon encountering a
   * ResponseStatusException.
   *
   * @param error A ResponseStatusException thrown by a REST controller.
   * @return A HTTP response including the relevant status code and a JSON body including the error
   *     message.
   */
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatusException(
      ResponseStatusException error) {
    Map<String, Object> response = new HashMap<>();

    // In production, we don't want to expose implementation details so display a generic message on
    // internal server error. However in a dev environment, it can be useful for these to be
    // returned.
    if (!devEnvironment && error.getStatusCode().value() == 500) {
      response.put("Message", "Error: " + INTERNAL_SERVER_ERROR_MESSAGE);
    } else {
      response.put("Message", "Error: " + error.getReason());
    }

    response.put("Error Code", error.getStatusCode().value());

    return ResponseEntity.status(error.getStatusCode()).body(response);
  }
}
