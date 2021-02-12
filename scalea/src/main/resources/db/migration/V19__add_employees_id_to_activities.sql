ALTER TABLE activities ADD COLUMN employee_id BIGINT NOT NULL;

ALTER TABLE activities ADD CONSTRAINT fk_employees FOREIGN KEY (employee_id) REFERENCES employees(id);