CREATE TABLE configuration (
  id              BIGSERIAL PRIMARY KEY,
  name        	  VARCHAR(255) NOT NULL UNIQUE,
  value  	      VARCHAR(255) NOT NULL,
  description	  VARCHAR(255) NULL
);

INSERT INTO configuration (name, value, description) VALUES ('image_path', 'C:\temp\img\', 'Path where to save the uploaded images in the system')