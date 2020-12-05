/* AREAS TABLE */
CREATE TABLE areas (
  id              BIGSERIAL PRIMARY KEY,
  created_at	  TIMESTAMP,
  updated_at	  TIMESTAMP,
  name	          VARCHAR(255) NOT NULL UNIQUE,
  enabled		  BOOLEAN NOT NULL DEFAULT TRUE,
  capacity	      INTEGER NOT NULL
);

ALTER TABLE areas ALTER COLUMN created_at SET DEFAULT NOW();
ALTER TABLE areas ALTER COLUMN updated_at SET DEFAULT NOW();

CREATE TRIGGER areas_updated
BEFORE UPDATE ON areas
FOR EACH ROW 
EXECUTE PROCEDURE update_updated_at();

/* EMPLOYEES TABLE */
CREATE TABLE employees (
  id              BIGSERIAL PRIMARY KEY,
  created_at	  TIMESTAMP,
  updated_at	  TIMESTAMP,
  firstname	  	  VARCHAR(255) NOT NULL,
  lastname	      VARCHAR(255) NOT NULL,
  account_number  VARCHAR(255),
  birthday	 	  TIMESTAMP,
  personal_number VARCHAR(255) NOT NULL UNIQUE,
  email 		  VARCHAR(255),
  phone_numberc   VARCHAR(255),
  status		  SMALLINT NOT NULL
);

ALTER TABLE employees ALTER COLUMN created_at SET DEFAULT NOW();
ALTER TABLE employees ALTER COLUMN updated_at SET DEFAULT NOW();

CREATE TRIGGER employees_updated
BEFORE UPDATE ON employees
FOR EACH ROW 
EXECUTE PROCEDURE update_updated_at();

/* VACANCIES TABLE */
CREATE TABLE vacancies (
  id              BIGSERIAL PRIMARY KEY,
  created_at	  TIMESTAMP,
  updated_at	  TIMESTAMP,
  number	      INTEGER NOT NULL,
  uuid			  VARCHAR(36) NOT NULL UNIQUE,
  area_id 		  BIGINT NOT NULL,
  employee_id 	  BIGINT,
  
  CONSTRAINT fk_area FOREIGN KEY(area_id) REFERENCES areas(id),
  CONSTRAINT fk_employee FOREIGN KEY(employee_id) REFERENCES employees(id)
);

ALTER TABLE vacancies ALTER COLUMN created_at SET DEFAULT NOW();
ALTER TABLE vacancies ALTER COLUMN updated_at SET DEFAULT NOW();

CREATE TRIGGER vacancies_updated
BEFORE UPDATE ON vacancies
FOR EACH ROW 
EXECUTE PROCEDURE update_updated_at();

/* PRODUCTS TABLE */
CREATE TABLE products (
  id              BIGSERIAL PRIMARY KEY,
  created_at	  TIMESTAMP,
  updated_at	  TIMESTAMP,
  name			  VARCHAR(255) NOT NULL, 
  price_per_kg	  DECIMAL(10,2) NOT NULL,
  image			  TEXT,
  product_id      BIGINT,
  
  CONSTRAINT fk_product FOREIGN KEY(product_id) REFERENCES products(id)
);

ALTER TABLE products ALTER COLUMN created_at SET DEFAULT NOW();
ALTER TABLE products ALTER COLUMN updated_at SET DEFAULT NOW();

CREATE TRIGGER products_updated
BEFORE UPDATE ON products
FOR EACH ROW 
EXECUTE PROCEDURE update_updated_at();

/* PROCESSES TABLE */
CREATE TABLE processes (
  id              BIGSERIAL PRIMARY KEY,
  created_at	  TIMESTAMP,
  updated_at	  TIMESTAMP,
  status		  SMALLINT NOT NULL,
  user_id		  BIGINT NOT NULL,
  area_id	      BIGINT NOT NULL,
  product_id      BIGINT NOT NULL,
  
  CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id),
  CONSTRAINT fk_area FOREIGN KEY(area_id) REFERENCES areas(id),
  CONSTRAINT fk_product FOREIGN KEY(product_id) REFERENCES products(id)
);

ALTER TABLE processes ALTER COLUMN created_at SET DEFAULT NOW();
ALTER TABLE processes ALTER COLUMN updated_at SET DEFAULT NOW();

CREATE TRIGGER processes_updated
BEFORE UPDATE ON processes
FOR EACH ROW 
EXECUTE PROCEDURE update_updated_at();

/* ACTIVITIES TABLE */
CREATE TABLE activities (
  id              BIGSERIAL PRIMARY KEY,
  created_at	  TIMESTAMP,
  updated_at	  TIMESTAMP,
  weight		  DECIMAL(16, 4) NOT NULL,
  product_id	  BIGINT NOT NULL,
  vacancy_id	  BIGINT NOT NULL,
  
  CONSTRAINT fk_vacancy FOREIGN KEY(vacancy_id) REFERENCES vacancies(id),
  CONSTRAINT fk_product FOREIGN KEY(product_id) REFERENCES products(id)
);

ALTER TABLE activities ALTER COLUMN created_at SET DEFAULT NOW();
ALTER TABLE activities ALTER COLUMN updated_at SET DEFAULT NOW();

CREATE TRIGGER activities_updated
BEFORE UPDATE ON activities
FOR EACH ROW 
EXECUTE PROCEDURE update_updated_at();
