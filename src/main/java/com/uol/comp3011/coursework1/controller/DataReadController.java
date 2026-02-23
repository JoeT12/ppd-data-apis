package com.uol.comp3011.coursework1.controller;

import com.uol.comp3011.coursework1.dal.entity.PpdDataRecord;
import com.uol.comp3011.coursework1.dal.repository.PpdRepository.AvgPricePerTown;
import com.uol.comp3011.coursework1.service.DataReadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/read")
public class DataReadController {
  // All data read API's should go into this class.
  private final DataReadService dataReadService;

  public DataReadController(DataReadService dataReadService) {
    this.dataReadService = dataReadService;
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/stats/area/lowestPriceAvgPerTownCity")
  public ResponseEntity<List<AvgPricePerTown>> lowestPriceAvgTownCity(
      @RequestParam(required = false) LocalDate from,
      @RequestParam(required = false) LocalDate to,
      @RequestParam(required = false) Integer limit) {
    try {
      List<AvgPricePerTown> avg = dataReadService.getLowestPriceAvgTownCity(from, to, limit);
      return ResponseEntity.ok().body(avg);
    } catch (SQLException error) {
      // Query returned no results - client passed in invalid value.
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error.getMessage());
    } catch (Exception error) {
      // Unexpected server side error. Good habit to put this at top level to prevent crashes.
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/allRecords")
  public ResponseEntity<List<PpdDataRecord>> getAllRecords(
      @RequestParam(name = "page_number", defaultValue = "0") int pageNum,
      @RequestParam(name = "page_size", defaultValue = "100") int pageSize) {
    try {
      List<PpdDataRecord> avg = dataReadService.getAllRecords(pageNum, pageSize);
      return ResponseEntity.ok().body(avg);
    } catch (Exception error) {
      // Unexpected server side error. Good habit to put this at top level to prevent crashes.
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }
}
