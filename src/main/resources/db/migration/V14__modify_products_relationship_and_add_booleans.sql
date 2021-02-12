ALTER TABLE products ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE products ADD COLUMN with_sub_products BOOLEAN NOT NULL DEFAULT TRUE;


DROP TABLE products_hierarchy;

ALTER TABLE products ADD COLUMN sub_product_id BIGINT NULL DEFAULT NULL;

ALTER TABLE products ADD CONSTRAINT fk_subproducts FOREIGN KEY (sub_product_id) REFERENCES products(id) ON DELETE SET NULL;