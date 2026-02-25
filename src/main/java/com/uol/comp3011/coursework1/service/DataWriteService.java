package com.uol.comp3011.coursework1.service;

import com.uol.comp3011.coursework1.dal.entity.PropertyTransaction;
import com.uol.comp3011.coursework1.dal.repository.PropertyTransactionRepository;

import com.uol.comp3011.coursework1.config.security.SecurityUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class DataWriteService {

  // Default logger.
  private static final Logger log = LoggerFactory.getLogger(DataWriteService.class);

  // Amount of rows we collect before writing to the database.
  private static final int BUFFER_SIZE = 100000;

  private final PropertyTransactionRepository ppd;
  // Time format as per the yearly PPD files.
  private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  public DataWriteService(PropertyTransactionRepository ppd) {
    this.ppd = ppd;
  }

  /**
   * Writes a yearly PPD CSV file to the database. Marked Transactional to ensure ACID database
   * principles followed when writing data into the database.
   *
   * <p>For efficiency, we use a buffer of size {@link #BUFFER_SIZE} records to ensure that we are
   * not writing every record to the database individually. This change was made after trying the
   * mentioned approach on a ~638,000 row dataset, which took over 10 mins to upload. Using the
   * buffer reduced this to under a minute.
   *
   * @param file A CSV file containing the yearly PPD data.
   * @throws Exception If any error occurs when writing the data to the database.
   */
  @Transactional
  public void uploadPpdYear(InputStream file) throws Exception {
    log.info("Beginning PPD Upload for User {}", SecurityUtils.getCurrentUsername());

    Reader fileReader = new InputStreamReader(file);
    Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder().build().parse(fileReader);

    // Buffer for writing records to database.
    ArrayList<PropertyTransaction> buffer = new ArrayList<>();

    for (CSVRecord r : records) {
      try {
        PropertyTransaction p = new PropertyTransaction();
        p.setTransactionUuid(UUID.fromString(r.get(0).replace("{", "").replace("}", "")));
        p.setPrice(Integer.parseInt(r.get(1)));
        p.setTransferDate(
            LocalDateTime.parse(r.get(2), fmt)
                .toLocalDate()); // Converted to date only since all times in yearly files are
        // 00:00.
        p.setPostcode(r.get(3));
        p.setPropertyTypeCode(r.get(4).charAt(0));
        p.setOldNewCode(r.get(5).charAt(0));
        p.setDurationCode(r.get(6).charAt(0));
        p.setPaon(r.get(7));
        p.setSaon(r.get(8));
        p.setStreet(r.get(9));
        p.setLocality(r.get(10));
        p.setTownCity(r.get(11));
        p.setDistrict(r.get(12));
        p.setCounty(r.get(13));
        p.setPpdCategoryCode(r.get(14).charAt(0));
        buffer.add(p);

        if (buffer.size() == BUFFER_SIZE) {
          ppd.saveAll(buffer);
          buffer.clear();
        }
      } catch (Exception error) {
        log.error("Error While Writing Yearly PPD File {}", error.getMessage());
        // Throw error back up to the controller to pass error onto user.
        throw error;
      }
    }

    // If any data remaining in buffer, save it.
    if (!buffer.isEmpty()) {
      ppd.saveAll(buffer);
      buffer.clear();
    }

    log.info("Completed PPD Upload for User {}", SecurityUtils.getCurrentUsername());
  }

  /**
   * Deletes a single PPD data record via its transaction id. Marked Transactional to ensure ACID
   * database principles followed when performing deletion.
   *
   * @param transactionId The id of the PPD data record we wish to delete.
   */
  @Transactional
  public void deletePpdRecord(String transactionId) {
    log.info(
        "Beginning Deletion of PPD Record {} for User {}",
        transactionId,
        SecurityUtils.getCurrentUsername());

    // Find record and delete.
    PropertyTransaction dataRecordToDelete =
        ppd.findByTransactionUuid(UUID.fromString(transactionId));
    ppd.delete(dataRecordToDelete);

    log.info(
        "Completed Deletion of PPD Record {} for User {}",
        transactionId,
        SecurityUtils.getCurrentUsername());
  }

  /**
   * Updates a given PPD record. Marked Transactional to ensure ACID database principles followed
   * when performing update.
   *
   * @param ppdRecord The PPD record to update.
   */
  @Transactional
  public void updatePpdRecord(PropertyTransaction ppdRecord) {
    log.info(
        "Beginning Update of PPD Record {} for User {}",
        ppdRecord.getTransactionUuid(),
        SecurityUtils.getCurrentUsername());

    // Find record and update.
    PropertyTransaction recordToUpdate = ppd.findByTransactionUuid(ppdRecord.getTransactionUuid());
    recordToUpdate.setPrice(ppdRecord.getPrice());
    recordToUpdate.setTransferDate(ppdRecord.getTransferDate());
    recordToUpdate.setPostcode(ppdRecord.getPostcode());
    recordToUpdate.setPropertyTypeCode(ppdRecord.getPropertyTypeCode());
    recordToUpdate.setOldNewCode(ppdRecord.getOldNewCode());
    recordToUpdate.setDurationCode(ppdRecord.getDurationCode());
    recordToUpdate.setPaon(ppdRecord.getPaon());
    recordToUpdate.setSaon(ppdRecord.getSaon());
    recordToUpdate.setStreet(ppdRecord.getStreet());
    recordToUpdate.setLocality(ppdRecord.getLocality());
    recordToUpdate.setTownCity(ppdRecord.getTownCity());
    recordToUpdate.setDistrict(ppdRecord.getDistrict());
    recordToUpdate.setCounty(ppdRecord.getCounty());
    recordToUpdate.setPpdCategoryCode(ppdRecord.getPpdCategoryCode());
    ppd.save(recordToUpdate);

    log.info(
        "Completed Update of PPD Record {} for User {}",
            ppdRecord.getTransactionUuid(),
        SecurityUtils.getCurrentUsername());
  }
}
