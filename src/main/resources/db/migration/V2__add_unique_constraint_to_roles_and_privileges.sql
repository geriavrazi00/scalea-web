ALTER TABLE roles ADD CONSTRAINT unique_role_name UNIQUE (name);

ALTER TABLE privileges ADD CONSTRAINT unique_privilege_name UNIQUE (name);