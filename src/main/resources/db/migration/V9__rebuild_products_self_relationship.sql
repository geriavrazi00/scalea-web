ALTER TABLE products DROP CONSTRAINT fk_product;
ALTER TABLE products DROP COLUMN product_id;

CREATE TABLE products_hierarchy (
  father_id      BIGINT NOT NULL,
  child_id		 BIGINT NOT NULL,
  
  CONSTRAINT fk_father_product FOREIGN KEY(father_id) REFERENCES products(id),
  CONSTRAINT fk_child_product FOREIGN KEY(child_id) REFERENCES products(id)
);