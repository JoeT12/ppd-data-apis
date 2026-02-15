package com.uol.comp3011.Coursework1.Service;

import com.uol.comp3011.Coursework1.Structs.AvgPricePerTown;
import com.uol.comp3011.Coursework1.Repository.PpdRepository;
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
  private final PpdRepository ppd;
  private final int maxLimit = 1000;

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
    log.info("DataReadService:getLowestPriceAvgTownCity:: INFO - BEGIN");

    // Sanitise the (optionally) passed limit to ensure it within allowed limits.
    // If it is not passed, then we just default the value to 10.
    int sanitisedLimit;
    if (limit != null && (limit > 0 && limit < maxLimit)) {
      sanitisedLimit = limit;
    } else {
      if (limit != null && maxLimit > limit) {
        // Log this for monitoring - if deployed could observe to see whether row limit needed
        // raising or not.
        log.error(
            "DataReadService:getLowestPriceAvgTownCity:: ERROR - Max limit exceeded with limit {}",
            limit);
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

    log.info("DataReadService:getLowestPriceAvgTownCity:: INFO - END");
    return results;
  }
}
