package com.uol.comp3011.Coursework1.Service;

import com.uol.comp3011.Coursework1.Entity.PpdDataRecord;
import com.uol.comp3011.Coursework1.Repository.PpdRepository;

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
  private final PpdRepository ppd;
  // Time format as per the PPD files.
  private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  // Amount of rows we collect before writing to the database.
  private final int bufferSize = 100000;

  public DataWriteService(PpdRepository ppd) {
    this.ppd = ppd;
  }

  /**
   * Writes a yearly PPD CSV file to the database.
   *
   * <p>For efficiency, we use a buffer of size {@link #bufferSize} records to ensure that we are
   * not writing every record to the database individually. This change was made after trying the
   * mentioned approach on a ~638,000 row dataset, which took over 10 mins to upload. Using the
   * buffer reduced this to under a minute.
   *
   * @param file A CSV file containing the yearly PPD data.
   * @throws IOException If any error occurs when writing the data to the database.
   */
  public void writeYearlyPpdFile(InputStream file) throws IOException {
    log.info("DataWriteService:writeYearlyPpdFile:: INFO - BEGIN");

    Reader fileReader = new InputStreamReader(file);
    Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder().build().parse(fileReader);

    // Buffer for writing records to database.
    ArrayList<PpdDataRecord> buffer = new ArrayList<>();

    for (CSVRecord r : records) {
      try {
        PpdDataRecord p = new PpdDataRecord();
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

        if (buffer.size() == bufferSize) {
          ppd.saveAll(buffer);
          buffer.clear();
        }
      } catch (Exception e) {
        log.error("DataWriteService:writeYearlyPpdFile:: ERROR - {}", e.getMessage());
        // Throw error back up to the controller to pass error onto user.
        throw e;
      }
    }

    if (!buffer.isEmpty()) {
      ppd.saveAll(buffer);
      buffer.clear();
    }
    log.info("DataWriteService::writeYearlyPpdFile:: INFO - END");
  }

}
