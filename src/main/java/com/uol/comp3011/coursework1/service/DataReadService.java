package com.uol.comp3011.coursework1.service;

import com.uol.comp3011.coursework1.dal.entity.PropertyTransaction;
import com.uol.comp3011.coursework1.dal.repository.PropertyTransactionRepository.AvgPricePerTown;
import com.uol.comp3011.coursework1.dal.repository.PropertyTransactionRepository;
import com.uol.comp3011.coursework1.dal.repository.PropertyTransactionRepository.AvgPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DataReadService {
  private static final Logger log = LoggerFactory.getLogger(DataReadService.class);
  private static final int RESPONSE_MAX_ROWS = 1000;

  private final PropertyTransactionRepository ppd;

  public DataReadService(PropertyTransactionRepository ppd) {
    this.ppd = ppd;
  }

  /**
   * Returns a list of the towns/cities in the UK that have the lowest average property price.
   *
   * @param from The date to start the query from.
   * @param to The date to end the query.
   * @param limit The maximum number of cheapest locations to return (default {@link
   *     #RESPONSE_MAX_ROWS}).
   * @return Ordered list of towns/cities by lowest average house sale price.
   * @throws SQLException If no results returned from query.
   */
  public List<AvgPricePerTown> getCheapestLocations(LocalDate from, LocalDate to, Integer limit)
      throws SQLException {
    log.info(
        "Beginning Retrieval of Towns with the Lowest Average Property Price Between Dates {} and {}",
        from,
        to);

    // Sanitise the (optionally) passed limit to ensure it within allowed limits.
    // If it is not passed, then we just default the value to the max limit.
    int sanitisedLimit = sanitiseResponseRowCount(limit);

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
        "Retrieval Completed for Towns With The Lowest Average Property Price Between Dates {} and {}",
        from,
        to);
    return results;
  }

  /**
   * Returns the average property price in a given postcode area.
   *
   * @param postcode The postcode of the area we wish to find the average property price for.
   * @return The average house price in a given postcode area.
   */
  public AvgPrice getAveragePriceByPostcode(String postcode) throws SQLException {
    log.info("Beginning Retrieval of Average Property Price in Postcode {}", postcode);

    AvgPrice avg = ppd.averagePriceByPostcode(postcode);

    if (avg.numSales() == 0) {
      throw new SQLException("Query Returned No Results");
    }

    log.info("Completed Retrieval of Average Property Price in Postcode {}", postcode);
    return avg;
  }

  /**
   * Returns the years for which PPD data has been imported into the database by a system
   * administrator.
   *
   * @return A list of years for which yearly PPD data has been imported.
   */
  public List<Integer> getPpdYearsAvailable() throws SQLException {
    log.info("Beginning Retrieval of PPD Years Imported");
    List<Integer> yearsAvailable = ppd.getAvailableYears();

    if (yearsAvailable == null || yearsAvailable.isEmpty()) {
      throw new SQLException("No Data In Database");
    }

    log.info("Completed Retrieval of PPD Years Imported");
    return yearsAvailable;
  }

  /**
   * Returns the average property price in a given town/city, for a particular property type.
   *
   * @param townCity The town to limit the data to.
   * @param propertyType The property type to limit the data to.
   * @return The average property price in the given town/city, for the given house type.
   */
  public AvgPrice getAveragePriceByPropertyType(String townCity, Character propertyType)
      throws SQLException {
    log.info(
        "Beginning Retrieval of Average Property Price for Type {} in Town/City {}",
        propertyType,
        townCity);
    AvgPrice avg = ppd.averagePriceByPropertyType(townCity, propertyType);

    if (avg.numSales() == 0) {
      throw new SQLException("Query returned no results");
    }

    log.info(
        "Completed Retrieval of Average Property Price for Type {} in Town/City {}",
        propertyType,
        townCity);

    return avg;
  }

  /**
   * Retrieves a single PPD record from the database.
   *
   * @param transactionId The id of the PPD record we wish to retrieve.
   * @return The retrieved record.
   */
  public PropertyTransaction getSingleRecord(String transactionId) throws SQLException {
    log.info("Beginning Retrieval of PPD Record {}", transactionId);
    PropertyTransaction ppdRecord = ppd.findByTransactionUuid(UUID.fromString(transactionId));

    if (ppdRecord == null) {
      throw new SQLException("Record could not be found");
    }

    log.info("Completed Retrieval of PPD Record {}", transactionId);
    return ppdRecord;
  }

  /**
   * Retrieves all available PPD data in the database. Uses pagination to limit the quantity of data
   * being sent over the network.
   *
   * @param pageNum The page number of the data to be retrieved from the database.
   * @param pageSize The size of each page (number of rows).
   * @return The page of PPD data items (streamed as a list).
   */
  public List<PropertyTransaction> getAllRecords(int pageNum, int pageSize) throws SQLException {
    log.info("Beginning Retrieval of PPD Page {}, Page Size = {}", pageNum, pageSize);

    int sanitisedPageSize = sanitiseResponseRowCount(pageSize);

    List<PropertyTransaction> results =
        ppd.findAll(PageRequest.of(pageNum, sanitisedPageSize)).stream().toList();
    if (results.isEmpty()) {
      throw new SQLException("No records could not be found");
    }

    log.info("Completed Retrieval of PPD Page {}, Page Size = {}", pageNum, pageSize);
    return results;
  }

  /**
   * Sanitises the number of database rows to be returned by the service to be within allowed
   * limits, preventing streaming of large amounts of data.
   *
   * @param rowCount The un-sanitised response row count from a user request.
   * @return The sanitised response row count after limits have been applied.
   */
  private int sanitiseResponseRowCount(Integer rowCount) {
    int sanitisedRowCount;
    if (rowCount != null && (rowCount > 0 && rowCount < RESPONSE_MAX_ROWS)) {
      // If within allowed limits, just return the original value.
      sanitisedRowCount = rowCount;
    } else {
      if (rowCount != null && RESPONSE_MAX_ROWS > rowCount) {
        // If above the limit, log so service log observers can determine whether the hard limit
        // needs raising.
        log.error("Maximum Row Retrieval Limit Exceeded with Limit {}", rowCount);
      }
      // If outside allowed limits, then set the limit to max limit.
      sanitisedRowCount = RESPONSE_MAX_ROWS;
    }
    return sanitisedRowCount;
  }
}
