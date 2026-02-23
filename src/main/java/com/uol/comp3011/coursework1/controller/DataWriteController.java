package com.uol.comp3011.coursework1.controller;

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
  @PostMapping("/admin/uploadYearlyData")
  public ResponseEntity<MessageResponse> uploadData(@RequestParam("file") MultipartFile file) {
    try {
      // Integrity checks
      if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "The file sent to the server was not in CSV format");
      } else if (file.isEmpty()) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Empty file sent to server!");
      }

      dataWriteService.writeYearlyPpdFile(file.getInputStream());
      return ResponseEntity.ok().body(new MessageResponse("File upload succeeded"));
    } catch (Exception error) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/admin/deleteDataRecord")
  public ResponseEntity<MessageResponse> deletePpdDataRecord(
      @RequestParam("transaction_id") String transactionId) {
    try {
      // Integrity checks
      if (transactionId == null || transactionId.isBlank()) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST, "transaction_id missing from request");
      }

      dataWriteService.deletePpdDataRecord(transactionId);
      return ResponseEntity.ok().body(new MessageResponse("Data record deleted successfully"));
    } catch (Exception error) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }

  //  @PreAuthorize("hasRole('ADMIN')")
  //  @PutMapping("/admin/updateDataRecord")
  //  public ResponseEntity<?> amendPpdDataRecord(@RequestParam("transaction_id") String
  // transactionId) {
  //    try {
  //      // Integrity checks
  //      if (transactionId == null || transactionId.isBlank()) {
  //        return ResponseEntity.badRequest()
  //                .body("transaction_id missing from request");
  //      }
  //
  //      dataWriteService.amendPpdDataRecord(transactionId);
  //      return ResponseEntity.ok().body("Data record deleted successfully");
  //    } catch (Exception e) {
  //      return ResponseEntity.internalServerError().body(e.getMessage());
  //    }
  //  }
}
