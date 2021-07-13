CREATE TABLE groups (
  id              BIGSERIAL PRIMARY KEY,
  created_at 	  TIMESTAMP DEFAULT NOW(),
  updated_at      TIMESTAMP DEFAULT NOW(),
  name            VARCHAR(255) NOT NULL,
  area_id		  BIGINT NOT NULL,
  default_group   BOOLEAN NOT NULL,
  CONSTRAINT fk_area FOREIGN KEY(area_id) REFERENCES areas(id)
);