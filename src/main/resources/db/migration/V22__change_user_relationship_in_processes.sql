ALTER TABLE processes ALTER COLUMN user_id DROP NOT NULL, ALTER COLUMN user_id DROP DEFAULT;

ALTER TABLE processes ADD CONSTRAINT fk_area FOREIGN KEY (area_id) REFERENCES areas(id) ON DELETE CASCADE;
ALTER TABLE processes ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL;