-- =========================
-- HM Land Registry PPD schema (PostgreSQL)
-- =========================

-- -------------------------
-- Reference tables
-- -------------------------
CREATE TABLE ref_property_type (
  code CHAR(1) PRIMARY KEY,
  description VARCHAR(50) NOT NULL
);

CREATE TABLE ref_old_new (
  code CHAR(1) PRIMARY KEY,
  description VARCHAR(50) NOT NULL
);

CREATE TABLE ref_duration (
  code CHAR(1) PRIMARY KEY,
  description VARCHAR(80) NOT NULL
);

CREATE TABLE ref_ppd_category (
  code CHAR(1) PRIMARY KEY,
  description VARCHAR(120) NOT NULL
);

CREATE TABLE ref_record_status (
  code CHAR(1) PRIMARY KEY,
  description VARCHAR(80) NOT NULL
);

-- -------------------------
-- Typed transactions table
-- -------------------------
CREATE TABLE ppd_transaction (

  transaction_uuid UUID PRIMARY KEY,

  price INT NOT NULL CHECK (price > 0),
  transfer_date DATE NOT NULL,

  postcode VARCHAR(10),

  property_type_code CHAR(1) NOT NULL,
  old_new_code CHAR(1),
  duration_code CHAR(1),

  paon VARCHAR(200),
  saon VARCHAR(200),
  street VARCHAR(200),
  locality VARCHAR(200),
  town_city VARCHAR(200),
  district VARCHAR(200),
  county VARCHAR(200),

  ppd_category_code CHAR(1) NOT NULL,
  record_status_code CHAR(1),

  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_ppd_txn_property_type
    FOREIGN KEY (property_type_code) REFERENCES ref_property_type(code),

  CONSTRAINT fk_ppd_txn_old_new
    FOREIGN KEY (old_new_code) REFERENCES ref_old_new(code),

  CONSTRAINT fk_ppd_txn_duration
    FOREIGN KEY (duration_code) REFERENCES ref_duration(code),

  CONSTRAINT fk_ppd_txn_category
    FOREIGN KEY (ppd_category_code) REFERENCES ref_ppd_category(code),

  CONSTRAINT fk_ppd_txn_record_status
    FOREIGN KEY (record_status_code) REFERENCES ref_record_status(code)
);

CREATE INDEX idx_ppd_txn_transfer_date ON ppd_transaction(transfer_date);
CREATE INDEX idx_ppd_txn_district ON ppd_transaction(district);
CREATE INDEX idx_ppd_txn_town_city ON ppd_transaction(town_city);
CREATE INDEX idx_ppd_txn_county ON ppd_transaction(county);
CREATE INDEX idx_ppd_txn_price ON ppd_transaction(price);
CREATE INDEX idx_ppd_txn_type_date ON ppd_transaction(property_type_code, transfer_date);