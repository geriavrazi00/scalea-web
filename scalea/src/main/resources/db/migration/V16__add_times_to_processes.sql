ALTER TABLE processes ADD COLUMN started_at TIMESTAMP NOT NULL;
ALTER TABLE processes ADD COLUMN stopped_at TIMESTAMP;