CREATE TABLE orders.products (
	id bigint PRIMARY KEY,
	name varchar NOT NULL,
	price bigint NOT NULL CHECK (price > 0), -- Current price of a product
	is_available boolean NOT NULL DEFAULT FALSE
);

COMMENT ON COLUMN orders.products.price IS 'Current price of a product';