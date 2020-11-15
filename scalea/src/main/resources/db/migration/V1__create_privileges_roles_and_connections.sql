CREATE TABLE privileges (
  id              BIGSERIAL PRIMARY KEY,
  name            VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
  id              BIGSERIAL PRIMARY KEY,
  name            VARCHAR(255) NOT NULL
);

CREATE TABLE roles_privileges (
	role_id       BIGINT NOT NULL,
	privilege_id  BIGINT NOT NULL,
	CONSTRAINT fk_role FOREIGN KEY(role_id) REFERENCES roles(id),
	CONSTRAINT fk_privilege FOREIGN KEY(privilege_id) REFERENCES privileges(id)
);

CREATE TABLE users_roles (
	user_id       BIGINT NOT NULL,
	role_id       BIGINT NOT NULL,
	CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id),
	CONSTRAINT fk_role FOREIGN KEY(role_id) REFERENCES roles(id)
);



