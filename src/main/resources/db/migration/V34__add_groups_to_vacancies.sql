ALTER TABLE vacancies ADD COLUMN group_id BIGINT NULL DEFAULT NULL;

ALTER TABLE vacancies ADD CONSTRAINT fk_groups FOREIGN KEY (group_id) REFERENCES groups(id);