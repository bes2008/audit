CREATE TABLE USER (
  ID   VARCHAR(100),
  NAME VARCHAR(50),
  AGE  INT
);

CREATE INDEX IDX_USER_01 on USER (NAME, AGE);
CREATE INDEX IDX_USER_02 on USER (AGE);
SHOW TABLES;

CREATE TABLE Customer
(
    ID varchar(50),
    name varchar(50),
    Address varchar(50),
    City varchar(50),
    Country varchar(25),
    BirthDate date,
    UPDATEDate date
);

Commit ;