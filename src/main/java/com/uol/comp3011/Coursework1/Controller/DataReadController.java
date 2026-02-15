package com.uol.comp3011.Coursework1.Controller;

import com.uol.comp3011.Coursework1.Structs.AvgPricePerTown;
import com.uol.comp3011.Coursework1.Service.DataReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/read")
public class DataReadController {
  // All data read API's should go into this class.
  private final DataReadService dataReadService;
  private static final Logger log = LoggerFactory.getLogger(DataReadController.class);

  public DataReadController(DataReadService dataReadService) {
    this.dataReadService = dataReadService;
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/stats/area/lowestPriceAvgPerTownCity")
  public ResponseEntity<?> lowestPriceAvgTownCity(
      @RequestParam(required = false) LocalDate from,
      @RequestParam(required = false) LocalDate to,
      @RequestParam(required = false) Integer limit) {
    try {
      List<AvgPricePerTown> avg = dataReadService.getLowestPriceAvgTownCity(from, to, limit);
      return ResponseEntity.ok().body(avg);
    } catch (SQLException e) {
      // Query returned no results - client passed in invalid value.
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      // Unexpected server side error. Good habit to put this at top level to prevent crashes.
      log.error(e.getMessage());
      return ResponseEntity.internalServerError().body(e.getMessage());
    }
  }
}
