-- details for the JDBCDaoImpl
DROP TABLE IF EXISTS users;
CREATE TABLE users (
  username VARCHAR(255),
  password VARCHAR(2048),
  enabled BOOLEAN
);

DROP TABLE IF EXISTS authorities;
CREATE TABLE authorities (
  username VARCHAR(255),
  authority VARCHAR(255)
);
