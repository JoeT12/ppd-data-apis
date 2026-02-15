package com.uol.comp3011.Coursework1.Controller;

import com.uol.comp3011.Coursework1.Service.DataWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
  public ResponseEntity<?> uploadData(@RequestParam("file") MultipartFile file) {
    try {
      // Integrity checks
      if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".csv")) {
        return ResponseEntity.badRequest()
            .body("The file sent to the server was not in CSV format!");
      } else if (file.isEmpty()) {
        return ResponseEntity.badRequest().body("Empty file sent to server!");
      }

      dataWriteService.writeYearlyPpdFile(file.getInputStream());
      return ResponseEntity.ok().body("File upload succeeded!");
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }
}
