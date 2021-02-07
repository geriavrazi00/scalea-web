CREATE TABLE users (
  id              BIGSERIAL PRIMARY KEY,
  username        VARCHAR(255) NOT NULL UNIQUE,
  password  	  VARCHAR(255) NOT NULL,
  firstname		  VARCHAR(255) NOT NULL,
  lastname	      VARCHAR(255) NOT NULL,
  phonenumber     VARCHAR(50) NOT NULL,
  identification  TEXT NOT NULL
);