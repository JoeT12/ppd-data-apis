package com.uol.comp3011.coursework1.service;

import com.uol.comp3011.coursework1.config.security.SecurityUtils;
import com.uol.comp3011.coursework1.dal.entity.PpdDataRecord;
import com.uol.comp3011.coursework1.dal.repository.PpdRepository.AvgPricePerTown;
import com.uol.comp3011.coursework1.dal.repository.PpdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class DataReadService {
  private static final Logger log = LoggerFactory.getLogger(DataReadService.class);
  private static final int MAX_ROWS_RETURNED = 1000;

  private final PpdRepository ppd;

  public DataReadService(PpdRepository ppd) {
    this.ppd = ppd;
  }

  /**
   * Creates and returns a list of towns/cities, ordered starting with the lowest average house sale
   * price.
   *
   * @param from The date to start the query from.
   * @param to The date to end the query.
   * @param limit The amount of rows to return (default 10).
   * @return Ordered list of towns/cities by lowest average house sale price.
   * @throws SQLException If no results returned from query.
   */
  public List<AvgPricePerTown> getLowestPriceAvgTownCity(
      LocalDate from, LocalDate to, Integer limit) throws SQLException {
    log.info(
        "Beginning Retrieval of Lowest Average House Sale Price Between Dates {} and {} for User {}",
        from,
        to,
        SecurityUtils.getCurrentUsername());

    // Sanitise the (optionally) passed limit to ensure it within allowed limits.
    // If it is not passed, then we just default the value to 10.
    int sanitisedLimit;
    if (limit != null && (limit > 0 && limit < MAX_ROWS_RETURNED)) {
      sanitisedLimit = limit;
    } else {
      if (limit != null && MAX_ROWS_RETURNED > limit) {
        // Log this for monitoring - if deployed could observe to see whether row limit needed
        // raising or not.
        log.error(
            "Maximum Row Retrieval Limit Exceeded with Limit {} by User {}",
            limit,
            SecurityUtils.getCurrentUsername());
      }
      // Fallback
      sanitisedLimit = 10;
    }

    // Different permutations as per the optionally passed parameters.
    List<AvgPricePerTown> results;
    if (from != null && to != null) {
      results = ppd.averagePriceByTownCityFromTo(from, to, PageRequest.of(0, sanitisedLimit));
    } else if (from == null && to == null) {
      results = ppd.averagePriceByTownCity(PageRequest.of(0, sanitisedLimit));
    } else if (to == null) {
      results = ppd.averagePriceByTownCityOnlyFrom(from, PageRequest.of(0, sanitisedLimit));
    } else {
      results = ppd.averagePriceByTownCityOnlyTo(to, PageRequest.of(0, sanitisedLimit));
    }

    if (results == null || results.isEmpty()) {
      throw new SQLException("Query returned no results");
    }

    log.info(
        "Retrieval Completed for the Lowest Average House Sale Price Between Dates {} and {} for User {}",
        from,
        to,
        SecurityUtils.getCurrentUsername());
    return results;
  }

  /**
   * Retrieves all available PPD data in the database. Uses pagination to limit results.
   *
   * @param pageNum The page number of the data to be retrieved from the database.
   * @param pageSize The size of each page (number of rows).
   * @return The page of PPD data items (streamed as a list).
   */
  public List<PpdDataRecord> getAllRecords(int pageNum, int pageSize) {
    log.info("Beginning Retrieval of Page {}, Page Size = {} for User {}", pageNum, pageSize, SecurityUtils.getCurrentUsername());
    List<PpdDataRecord> results = ppd.findAll(PageRequest.of(pageNum, pageSize)).stream().toList();
    log.info("Completed Retrieval of Page {}, Page Size = {} for User {}", pageNum, pageSize, SecurityUtils.getCurrentUsername());
    return results;
  }
}
