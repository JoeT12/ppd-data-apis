package com.uol.comp3011.Coursework1.Repository;

import com.uol.comp3011.Coursework1.Structs.AvgPricePerTown;
import com.uol.comp3011.Coursework1.Entity.PpdDataRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

public interface PpdRepository extends JpaRepository<PpdDataRecord, Long> {
  List<PpdDataRecord> findByTownCity(String town_city);

  @Query(
      "SELECT p.townCity, AVG(p.price) FROM PpdDataRecord p GROUP BY p.townCity ORDER BY AVG(p.price) ASC")
  List<AvgPricePerTown> averagePriceByTownCity(Pageable pageable);

  @Query(
      "SELECT p.townCity, AVG(p.price) FROM PpdDataRecord p WHERE transferDate > ?1 and transferDate < ?2 GROUP BY p.townCity ORDER BY AVG(p.price) ASC")
  List<AvgPricePerTown> averagePriceByTownCityFromTo(
      LocalDate from, LocalDate to, Pageable pageable);

  @Query(
      "SELECT p.townCity, AVG(p.price) FROM PpdDataRecord p WHERE transferDate > ?1 GROUP BY p.townCity ORDER BY AVG(p.price) ASC")
  List<AvgPricePerTown> averagePriceByTownCityOnlyFrom(LocalDate from, Pageable pageable);

  @Query(
      "SELECT p.townCity, AVG(p.price) FROM PpdDataRecord p WHERE transferDate < ?1 GROUP BY p.townCity ORDER BY AVG(p.price) ASC")
  List<AvgPricePerTown> averagePriceByTownCityOnlyTo(LocalDate to, Pageable pageable);
}
