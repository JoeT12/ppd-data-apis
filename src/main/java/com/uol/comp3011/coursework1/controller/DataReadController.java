package com.uol.comp3011.coursework1.controller;

import com.uol.comp3011.coursework1.dal.entity.PropertyTransaction;
import com.uol.comp3011.coursework1.dal.repository.PropertyTransactionRepository.AvgPrice;
import com.uol.comp3011.coursework1.dal.repository.PropertyTransactionRepository.AvgPricePerTown;
import com.uol.comp3011.coursework1.dto.response.AvgPriceByPostcodeResponse;
import com.uol.comp3011.coursework1.dto.response.AvgPriceByPropertyTypeResponse;
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
  @GetMapping("/analytics/cheapest-locations")
  public ResponseEntity<List<AvgPricePerTown>> getCheapestLocations(
      @RequestParam(required = false) LocalDate from,
      @RequestParam(required = false) LocalDate to,
      @RequestParam(required = false) Integer limit) {
    try {
      List<AvgPricePerTown> avg = dataReadService.getCheapestLocations(from, to, limit);
      return ResponseEntity.ok().body(avg);
    } catch (SQLException error) {
      // Query returned no results - client passed in invalid value.
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error.getMessage());
    } catch (Exception error) {
      // Unexpected server side error.
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping(
      value = "/analytics/average-price",
      params = {"postcode"})
  public ResponseEntity<AvgPriceByPostcodeResponse> getAveragePriceByPostcode(
      @RequestParam String postcode) {
    try {
      AvgPrice avg = dataReadService.getAveragePriceByPostcode(postcode);
      return ResponseEntity.ok()
          .body(new AvgPriceByPostcodeResponse(postcode, avg.avgPrice(), avg.numSales()));
    } catch (SQLException error) {
      // Query returned no results - client passed in invalid value.
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error.getMessage());
    } catch (Exception error) {
      // Unexpected server side error.
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping(
      value = "/analytics/average-price",
      params = {"town_city", "property_type"})
  public ResponseEntity<AvgPriceByPropertyTypeResponse> getAveragePriceByPropertyType(
      @RequestParam(name = "town_city") String townCity,
      @RequestParam(name = "property_type") Character propertyType) {
    try {
      AvgPrice avg = dataReadService.getAveragePriceByPropertyType(townCity, propertyType);
      return ResponseEntity.ok()
          .body(new AvgPriceByPropertyTypeResponse(townCity, propertyType, avg.avgPrice()));
    } catch (SQLException error) {
      // Query returned no results - client passed in invalid value.
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error.getMessage());
    } catch (Exception error) {
      // Unexpected server side error.
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/data/available-years-ppd")
  public ResponseEntity<List<Integer>> getPpdYearsAvailable() {
    try {
      List<Integer> ppdYearsAvailable = dataReadService.getPpdYearsAvailable();
      return ResponseEntity.ok().body(ppdYearsAvailable);
    } catch (Exception error) {
      // Unexpected server side error.
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/data/single-record")
  public ResponseEntity<PropertyTransaction> getSinglePpdRecord(
      @RequestParam("transaction_id") String transactionId) {
    try {
      return ResponseEntity.ok().body(dataReadService.getSingleRecord(transactionId));
    } catch (SQLException error) {
      // Query returned no results - client passed in invalid value.
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error.getMessage());
    } catch (Exception error) {
      // Unexpected server side error.
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/data/all-records")
  public ResponseEntity<List<PropertyTransaction>> getAllPpdRecords(
      @RequestParam(name = "page_number", defaultValue = "0") int pageNum,
      @RequestParam(name = "page_size", defaultValue = "100") int pageSize) {
    try {
      List<PropertyTransaction> avg = dataReadService.getAllRecords(pageNum, pageSize);
      return ResponseEntity.ok().body(avg);
    } catch (Exception error) {
      // Unexpected server side error.
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, error.getMessage());
    }
  }
}
