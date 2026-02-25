package com.uol.comp3011.coursework1.controller;

import com.uol.comp3011.coursework1.dal.entity.PropertyTransaction;
import com.uol.comp3011.coursework1.dto.response.MessageResponse;
import com.uol.comp3011.coursework1.service.DataWriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/write")
public class DataWriteController {
  // Read/Update/Delete API's To go here.
  private final DataWriteService dataWriteService;

  public DataWriteController(DataWriteService dataWriteService) {
    this.dataWriteService = dataWriteService;
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/admin/upload-ppd-year")
  public ResponseEntity<MessageResponse> uploadPpdYear(@RequestParam("file") MultipartFile file) {
    try {
      // Validate request
      if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "The file sent to the server was not in CSV format");
      } else if (file.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty file sent to server!");
      }

      dataWriteService.uploadPpdYear(file.getInputStream());
      return ResponseEntity.ok().body(new MessageResponse("File upload succeeded"));
    } catch (Exception error) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not write PPD data to the database - view service logs for further details");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/admin/delete-ppd-record")
  public ResponseEntity<MessageResponse> deletePpdRecord(
      @RequestParam("transaction_id") String transactionId) {
    try {
      // Validate request
      if (transactionId == null || transactionId.isBlank()) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "transaction_id missing from request");
      }

      dataWriteService.deletePpdRecord(transactionId);
      return ResponseEntity.ok().body(new MessageResponse("Data record deleted successfully"));
    } catch (Exception error) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/admin/update-ppd-record")
  public ResponseEntity<MessageResponse> updatePpdRecord(
      @RequestBody PropertyTransaction updatedRecord) {
    try {
      // Validate request
      if (updatedRecord == null) {
        return ResponseEntity.badRequest().body(new MessageResponse("Missing Request Body"));
      }

      dataWriteService.updatePpdRecord(updatedRecord);
      return ResponseEntity.ok().body(new MessageResponse("Data record updated successfully"));
    } catch (Exception error) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }
}
