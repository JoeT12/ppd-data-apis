package com.uol.comp3011.coursework1.dal.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ppd_transaction")
public class PropertyTransaction {

  /* - @Id Tells JPA that this column is going to be the primary key of the table.
     - @GeneratedValue(strategy = GenerationType.IDENTITY) Tells JPA that we are automatically generating the id in the database,
     and therefore that it should not be expecting it when we insert new rows.
  */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "transaction_uuid")
  private UUID transactionUuid;

  @Column private int price;

  @Column(name = "transfer_date")
  private LocalDate transferDate;

  @Column private String postcode;

  @Column(name = "property_type_code")
  private Character propertyTypeCode;

  @Column(name = "old_new_code")
  private Character oldNewCode;

  @Column(name = "duration_code")
  private Character durationCode;

  @Column private String paon;

  @Column private String saon;

  @Column private String street;

  @Column private String locality;

  @Column(name = "town_city")
  private String townCity;

  @Column private String district;

  @Column private String county;

  @Column(name = "ppd_category_code")
  private Character ppdCategoryCode;

  @Column(name = "record_status_code")
  private Character recordStatusCode;

  /* There is no way to tell JPA that we are automatically generating a timestamp, so to workaround we just tell
     JPA that the column is not insertable. This will stop it from throwing errors when we provide a row without
     a created_at column.
  */
  @Column(name = "created_at", insertable = false)
  private Instant createdAt;
}
