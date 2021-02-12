CREATE FUNCTION update_updated_at() RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at := NOW();
	RETURN NEW;
END; $$
LANGUAGE plpgsql;

CREATE TRIGGER users_updated
BEFORE UPDATE ON users
FOR EACH ROW 
EXECUTE PROCEDURE update_updated_at();

CREATE TRIGGER roles_updated
BEFORE UPDATE ON roles
FOR EACH ROW 
EXECUTE PROCEDURE update_updated_at();

CREATE TRIGGER privileges_updated
BEFORE UPDATE ON privileges
FOR EACH ROW 
EXECUTE PROCEDURE update_updated_at();