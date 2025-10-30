-- ===========================================================
--  SOUMS Oracle User Management System
--  Developed by: Masam Srikanth
--  Database Setup Script
-- ===========================================================

-- Drop existing objects (optional, to reset)
BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE transactions CASCADE CONSTRAINTS';
  EXECUTE IMMEDIATE 'DROP TABLE accounts CASCADE CONSTRAINTS';
  EXECUTE IMMEDIATE 'DROP TABLE users CASCADE CONSTRAINTS';
  EXECUTE IMMEDIATE 'DROP SEQUENCE user_seq';
  EXECUTE IMMEDIATE 'DROP SEQUENCE account_seq';
  EXECUTE IMMEDIATE 'DROP SEQUENCE trans_seq';
EXCEPTION
  WHEN OTHERS THEN NULL;
END;
/

-- ======================
-- USERS TABLE
-- ======================
CREATE TABLE users (
  user_id NUMBER PRIMARY KEY,
  username VARCHAR2(50) UNIQUE NOT NULL,
  password VARCHAR2(200) NOT NULL,
  role VARCHAR2(10) DEFAULT 'customer',
  status VARCHAR2(10) DEFAULT 'ACTIVE'
);

CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER user_trigger
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
  SELECT user_seq.NEXTVAL INTO :NEW.user_id FROM dual;
END;
/

-- ======================
-- ACCOUNTS TABLE
-- ======================
CREATE TABLE accounts (
  acc_no NUMBER PRIMARY KEY,
  user_id NUMBER REFERENCES users(user_id),
  balance NUMBER DEFAULT 0,
  status VARCHAR2(10) DEFAULT 'ACTIVE'
);

CREATE SEQUENCE account_seq START WITH 1001 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER account_trigger
BEFORE INSERT ON accounts
FOR EACH ROW
BEGIN
  SELECT account_seq.NEXTVAL INTO :NEW.acc_no FROM dual;
END;
/

-- ======================
-- TRANSACTIONS TABLE
-- ======================
CREATE TABLE transactions (
  txn_id NUMBER PRIMARY KEY,
  from_acc NUMBER,
  to_acc NUMBER,
  amount NUMBER,
  txn_type VARCHAR2(20),
  txn_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE trans_seq START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER trans_trigger
BEFORE INSERT ON transactions
FOR EACH ROW
BEGIN
  SELECT trans_seq.NEXTVAL INTO :NEW.txn_id FROM dual;
END;
/

-- ======================
-- DEFAULT ADMIN USER
-- ======================
INSERT INTO users (username, password, role, status)
VALUES ('admin', 'admin123', 'admin', 'ACTIVE');

COMMIT;
