ALTER TABLE activities RENAME COLUMN "timestamp" TO "date";
ALTER TABLE activities ALTER COLUMN date TYPE TIMESTAMP USING date::timestamp without time zone;

ALTER TABLE activities ADD COLUMN area_id BIGINT NOT NULL;
ALTER TABLE activities ADD COLUMN user_id BIGINT NOT NULL;

ALTER TABLE activities ADD CONSTRAINT fk_areas FOREIGN KEY (area_id) REFERENCES areas(id);
ALTER TABLE activities ADD CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users(id);