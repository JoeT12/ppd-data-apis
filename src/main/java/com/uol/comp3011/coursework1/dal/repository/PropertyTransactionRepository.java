package com.uol.comp3011.coursework1.dal.repository;

import com.uol.comp3011.coursework1.dal.entity.PropertyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PropertyTransactionRepository extends JpaRepository<PropertyTransaction, Long> {
  record AvgPricePerTown(String townCity, Double price) {}

  record AvgPrice(Double avgPrice, Long numSales) {}

  PropertyTransaction findByTransactionUuid(UUID transactionId);

  @Query(
      "SELECT p.townCity, AVG(p.price) FROM PropertyTransaction p GROUP BY p.townCity ORDER BY AVG(p.price) ASC")
  List<AvgPricePerTown> averagePriceByTownCity(Pageable pageable);

  @Query(
      "SELECT p.townCity, AVG(p.price) FROM PropertyTransaction p WHERE transferDate > ?1 and transferDate < ?2 GROUP BY p.townCity ORDER BY AVG(p.price) ASC")
  List<AvgPricePerTown> averagePriceByTownCityFromTo(
      LocalDate from, LocalDate to, Pageable pageable);

  @Query(
      "SELECT p.townCity, AVG(p.price) FROM PropertyTransaction p WHERE transferDate > ?1 GROUP BY p.townCity ORDER BY AVG(p.price) ASC")
  List<AvgPricePerTown> averagePriceByTownCityOnlyFrom(LocalDate from, Pageable pageable);

  @Query(
      "SELECT p.townCity, AVG(p.price) FROM PropertyTransaction p WHERE transferDate < ?1 GROUP BY p.townCity ORDER BY AVG(p.price) ASC")
  List<AvgPricePerTown> averagePriceByTownCityOnlyTo(LocalDate to, Pageable pageable);

  @Query("SELECT AVG(p.price), COUNT(p) FROM PropertyTransaction p WHERE p.postcode = ?1")
  AvgPrice averagePriceByPostcode(String postcode);

  @Query(
      "SELECT AVG(p.price), COUNT(p) FROM PropertyTransaction p WHERE p.townCity = ?1 and p.propertyTypeCode = ?2")
  AvgPrice averagePriceByPropertyType(String townCity, Character propertyType);

  @Query(
      "SELECT DISTINCT YEAR(p.transferDate) FROM PropertyTransaction p ORDER BY YEAR(p.transferDate)")
  List<Integer> getAvailableYears();
}
